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

data class GitHubProfile(
    val login: String,
    val name: String?,
)

data class OperationResult(
    val success: Boolean,
    val message: String,
)

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
        runCatching {
            val payload = JSONObject()
                .put("name", name)
                .put("description", description)
                .put("private", isPrivate)
                .put("auto_init", true)
            request("/user/repos", token, "POST", payload.toString()).requireSuccess()
        }.fold(
            onSuccess = { OperationResult(true, "Repository created") },
            onFailure = { OperationResult(false, it.message ?: "Unable to create repository") },
        )
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
        if (commits.isEmpty()) return@withContext OperationResult(false, "No contributions to publish")

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
            onSuccess = { OperationResult(true, "Contributions published") },
            onFailure = { OperationResult(false, it.message ?: "Unable to publish contributions") },
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
            setRequestProperty("Content-Type", "application/json")
            if (body != null) {
                doOutput = true
                outputStream.bufferedWriter().use { it.write(body) }
            }
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            GitHubResponse(
                connection.responseCode,
                stream?.bufferedReader()?.use { it.readText() }.orEmpty(),
            )
        } finally {
            connection.disconnect()
        }
    }
}

private data class GitHubResponse(val statusCode: Int, val body: String) {
    fun requireSuccess(): GitHubResponse {
        if (statusCode !in 200..299) {
            val message = runCatching { JSONObject(body).optString("message") }.getOrNull()
            error(message?.takeIf { it.isNotBlank() } ?: "GitHub returned HTTP $statusCode")
        }
        return this
    }
}
