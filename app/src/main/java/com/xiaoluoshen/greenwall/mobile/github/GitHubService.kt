package com.xiaoluoshen.greenwall.mobile.github

import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Base64

private const val API_BASE_URL = "https://api.github.com"
private const val GITHUB_API_VERSION = "2022-11-28"

data class GitHubProfile(
    val login: String,
    val name: String?,
)

data class OperationResult(
    val success: Boolean,
    val message: String,
)

internal object RepositoryNameValidator {
    fun validate(value: String): String? {
        if (value.isBlank()) return "仓库名称不能为空"
        if (value != value.trim()) return "仓库名称不能以空格开头或结尾"
        if (value == "." || value == "..") return "仓库名称不可用"
        if (value.length > 100) return "仓库名称不能超过 100 个字符"
        if (value.any { it.isWhitespace() || it == '/' || it == '\\' || it.isISOControl() }) {
            return "仓库名称不能包含空格、斜杠或控制字符"
        }
        return null
    }
}

internal fun repositoryCreationFailureMessage(statusCode: Int, apiMessage: String?): String {
    val detail = apiMessage?.takeIf { it.isNotBlank() }
    return when (statusCode) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> "GitHub 认证已失效，请退出后使用新的个人访问令牌重新登录"
        HttpURLConnection.HTTP_FORBIDDEN -> "令牌没有创建仓库的权限。经典令牌需要 repo 权限；细粒度令牌需要 Administration 写入权限"
        422 -> {
            if (detail?.contains("already exists", ignoreCase = true) == true) {
                "该仓库名称已存在，请使用其他名称"
            } else {
                "GitHub 拒绝创建仓库${detail?.let { "：$it" } ?: "，请检查仓库名称或账户限制"}"
            }
        }
        429 -> "GitHub 请求过于频繁，请稍后再试"
        else -> "创建仓库失败（HTTP $statusCode）${detail?.let { "：$it" } ?: ""}"
    }
}

class GitHubService {
    suspend fun validateToken(token: String): Result<GitHubProfile> = withContext(Dispatchers.IO) {
        runCatching {
            val response = request("/user", token)
            response.requireSuccess()
            val json = JSONObject(response.body)
            GitHubProfile(
                login = json.getString("login"),
                name = json.optString("name").takeIf { it.isNotBlank() },
            )
        }
    }

    suspend fun createRepository(
        token: String,
        name: String,
        description: String,
        isPrivate: Boolean,
    ): OperationResult = withContext(Dispatchers.IO) {
        val repositoryName = name.trim()
        RepositoryNameValidator.validate(repositoryName)?.let { validationError ->
            return@withContext OperationResult(false, validationError)
        }

        runCatching {
            val payload = JSONObject()
                .put("name", repositoryName)
                .put("description", description.trim())
                .put("private", isPrivate)
                .put("auto_init", true)
            val response = request("/user/repos", token, "POST", payload.toString())
            if (!response.isSuccess) {
                return@runCatching OperationResult(
                    false,
                    repositoryCreationFailureMessage(response.statusCode, response.apiMessage()),
                )
            }
            OperationResult(true, "仓库已创建")
        }.getOrElse { error ->
            OperationResult(false, "创建仓库时网络连接失败：${error.message ?: "请检查网络后重试"}")
        }
    }

    suspend fun publishContributions(
        token: String,
        owner: String,
        repository: String,
        contributions: ContributionMap,
        onProgress: (Int, Int) -> Unit,
    ): OperationResult = withContext(Dispatchers.IO) {
        val commits = contributions.entries
            .filter { it.value > 0 }
            .sortedBy { it.key }
            .flatMap { entry -> List(entry.value) { entry.key to it } }
        if (commits.isEmpty()) return@withContext OperationResult(false, "没有可提交的贡献记录")

        runCatching {
            val profile = validateToken(token).getOrThrow()
            val email = getPrimaryEmail(token, profile.login)
            commits.forEachIndexed { completed, (date, index) ->
                putContributionFile(
                    token = token,
                    owner = owner,
                    repository = repository,
                    date = date,
                    index = index,
                    authorName = profile.name ?: profile.login,
                    email = email,
                    countForDay = contributions.getValue(date),
                )
                onProgress(completed + 1, commits.size)
                Thread.sleep(100)
            }
        }.fold(
            onSuccess = { OperationResult(true, "贡献提交完成") },
            onFailure = { error ->
                OperationResult(false, error.message ?: "贡献提交失败，请检查令牌的 Contents 写入权限")
            },
        )
    }

    private fun getPrimaryEmail(token: String, login: String): String {
        return runCatching {
            val response = request("/user/emails", token)
            response.requireSuccess()
            val emails = JSONArray(response.body)
            val candidates = (0 until emails.length()).map { emails.getJSONObject(it) }
            candidates.firstOrNull { it.optBoolean("primary") }?.getString("email")
                ?: candidates.firstOrNull { it.optBoolean("verified") }?.getString("email")
                ?: candidates.firstOrNull()?.getString("email")
                ?: "$login@users.noreply.github.com"
        }.getOrDefault("$login@users.noreply.github.com")
    }

    private fun putContributionFile(
        token: String,
        owner: String,
        repository: String,
        date: String,
        index: Int,
        authorName: String,
        email: String,
        countForDay: Int,
    ) {
        val timestamp = LocalDate.parse(date)
            .atStartOfDay()
            .plusMinutes(index.toLong())
            .toInstant(ZoneOffset.UTC)
            .toString()
        val filePath = "contributions/$date/${index.toString().padStart(4, '0')}.txt"
        val content = listOf(
            "GreenWall Contribution",
            "Date: $date",
            "Index: ${index + 1}/$countForDay",
            "Author: $authorName",
        ).joinToString("\n")
        val payload = JSONObject()
            .put("message", "chore: contribution on $date (${index + 1}/$countForDay)")
            .put("content", Base64.getEncoder().encodeToString(content.toByteArray()))
            .put("committer", JSONObject().put("name", authorName).put("email", email).put("date", timestamp))
            .put("author", JSONObject().put("name", authorName).put("email", email).put("date", timestamp))

        request("/repos/$owner/$repository/contents/$filePath", token, "PUT", payload.toString()).requireSuccess()
    }

    private fun request(
        path: String,
        token: String,
        method: String = "GET",
        body: String? = null,
    ): GitHubResponse {
        val connection = (URL("$API_BASE_URL$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 30_000
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", GITHUB_API_VERSION)
            setRequestProperty("Content-Type", "application/json")
            if (body != null) {
                doOutput = true
                outputStream.bufferedWriter().use { it.write(body) }
            }
        }

        return try {
            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            GitHubResponse(
                statusCode = statusCode,
                body = stream?.bufferedReader()?.use { it.readText() }.orEmpty(),
            )
        } finally {
            connection.disconnect()
        }
    }
}

private data class GitHubResponse(
    val statusCode: Int,
    val body: String,
) {
    val isSuccess: Boolean
        get() = statusCode in 200..299

    fun apiMessage(): String? = runCatching {
        JSONObject(body).optString("message").takeIf { it.isNotBlank() }
    }.getOrNull()

    fun requireSuccess(): GitHubResponse {
        if (!isSuccess) {
            error(apiMessage() ?: "GitHub returned HTTP $statusCode")
        }
        return this
    }
}
