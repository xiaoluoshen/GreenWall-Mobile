package com.xiaoluoshen.greenwall.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xiaoluoshen.greenwall.mobile.data.GitHubSession
import com.xiaoluoshen.greenwall.mobile.data.SecureSessionRepository
import com.xiaoluoshen.greenwall.mobile.domain.ContributionMap
import com.xiaoluoshen.greenwall.mobile.github.GitHubService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val token: String = "",
    val isTokenVisible: Boolean = false,
    val session: GitHubSession? = null,
    val isBusy: Boolean = false,
    val message: String? = null,
    val language: AppLanguage = AppLanguage.Chinese,
    val syncProgress: Pair<Int, Int>? = null,
)

enum class AppLanguage { Chinese, English }

class SettingsViewModel(
    private val sessionRepository: SecureSessionRepository,
    private val githubService: GitHubService,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState(session = sessionRepository.read()))
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun updateToken(token: String) {
        _state.value = _state.value.copy(token = token)
    }

    fun toggleTokenVisibility() {
        _state.value = _state.value.copy(isTokenVisible = !_state.value.isTokenVisible)
    }

    fun toggleLanguage() {
        val language = if (_state.value.language == AppLanguage.Chinese) AppLanguage.English else AppLanguage.Chinese
        _state.value = _state.value.copy(language = language)
    }

    fun login() {
        val token = _state.value.token.trim()
        if (token.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, message = null)
            githubService.validateToken(token).fold(
                onSuccess = { profile ->
                    val session = GitHubSession(token, profile.login, profile.name)
                    sessionRepository.save(session)
                    _state.value = _state.value.copy(
                        session = session,
                        token = "",
                        isBusy = false,
                        message = "登录成功",
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isBusy = false,
                        message = error.message ?: "登录失败，请检查令牌",
                    )
                },
            )
        }
    }

    fun logout() {
        sessionRepository.clear()
        _state.value = _state.value.copy(session = null, token = "", isTokenVisible = false)
    }

    fun publish(
        repository: String,
        description: String,
        isPrivate: Boolean,
        contributions: ContributionMap,
    ) {
        val session = _state.value.session ?: run {
            _state.value = _state.value.copy(message = "请先登录 GitHub")
            return
        }
        if (repository.isBlank()) {
            _state.value = _state.value.copy(message = "请输入仓库名称")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, message = null, syncProgress = 0 to 0)
            val repositoryResult = githubService.createRepository(
                token = session.token,
                name = repository,
                description = description,
                isPrivate = isPrivate,
            )
            if (!repositoryResult.success) {
                _state.value = _state.value.copy(isBusy = false, message = repositoryResult.message)
                return@launch
            }

            val publishResult = githubService.publishContributions(
                token = session.token,
                owner = session.login,
                repository = repository,
                contributions = contributions,
            ) { current, total ->
                _state.value = _state.value.copy(syncProgress = current to total)
            }
            _state.value = _state.value.copy(
                isBusy = false,
                message = publishResult.message,
                syncProgress = null,
            )
        }
    }

    fun consumeMessage() {
        _state.value = _state.value.copy(message = null)
    }

    class Factory(
        private val sessionRepository: SecureSessionRepository,
        private val githubService: GitHubService,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
            return SettingsViewModel(sessionRepository, githubService) as T
        }
    }
}
