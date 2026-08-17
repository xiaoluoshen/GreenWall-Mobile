package com.xiaoluoshen.greenwall.mobile.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val TOKEN_KEY = "github_token"
private const val LOGIN_KEY = "github_login"
private const val NAME_KEY = "github_name"

data class GitHubSession(
    val token: String,
    val login: String,
    val name: String?,
)

class SecureSessionRepository(context: Context) {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        "greenwall_secure_session",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun read(): GitHubSession? {
        val token = preferences.getString(TOKEN_KEY, null) ?: return null
        val login = preferences.getString(LOGIN_KEY, null) ?: return null
        return GitHubSession(token, login, preferences.getString(NAME_KEY, null))
    }

    fun save(session: GitHubSession) {
        preferences.edit()
            .putString(TOKEN_KEY, session.token)
            .putString(LOGIN_KEY, session.login)
            .putString(NAME_KEY, session.name)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
