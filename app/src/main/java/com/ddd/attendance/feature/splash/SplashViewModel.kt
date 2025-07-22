package com.ddd.attendance.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.accounts.GetAccessTokenUseCase
import com.ddd.attendance.core.domain.usecase.accounts.GetEmailUseCase
import com.ddd.attendance.core.domain.usecase.accounts.LoginEmailUseCase
import com.ddd.attendance.core.domain.usecase.profiles.GetProfileMeUseCase
import com.ddd.attendance.feature.login.model.LoginEmailUiState
import com.ddd.attendance.feature.login.model.ProfileMeUiState
import com.ddd.attendance.feature.main.screen.ScreenName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getEmailUseCase: GetEmailUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val loginEmailUseCase: LoginEmailUseCase,
    private val getProfileMeUseCase: GetProfileMeUseCase
) : ViewModel() {

    private val _screenType = MutableStateFlow<String>("")
    val screenType: StateFlow<String> = _screenType.asStateFlow()

    private val _loginEmailUiState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Empty)
    val loginEmailUiState: StateFlow<LoginEmailUiState> = _loginEmailUiState.asStateFlow()

    private val _profileMeUiState = MutableStateFlow<ProfileMeUiState>(ProfileMeUiState.Empty)
    val profileMeUiState: StateFlow<ProfileMeUiState> = _profileMeUiState.asStateFlow()

    init {
        checkUserCredentials()
    }

    private fun checkUserCredentials() {
        viewModelScope.launch {
            combine(getEmailUseCase(), getAccessTokenUseCase()) { email, token ->
                email to token
            }.collect { (email, token) ->
                if (email.isBlank() || token.isBlank()) {
                    _screenType.value = ScreenName.LOGIN.name
                } else {
                    attemptLogin(email)
                }
            }
        }
    }

    private fun attemptLogin(email: String) {
        viewModelScope.launch {
            loginEmailUseCase(email)
                .onStart { _loginEmailUiState.value = LoginEmailUiState.Loading }
                .filterNotNull()
                .map { LoginEmailUiState.Success(it) as LoginEmailUiState }
                .catch { e -> _loginEmailUiState.value = LoginEmailUiState.Error(e.message ?: "Unknown error") }
                .collect { state ->
                    _loginEmailUiState.value = state
                    if (state is LoginEmailUiState.Success) fetchUserProfile()
                }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            getProfileMeUseCase()
                .onStart { _profileMeUiState.value = ProfileMeUiState.Loading }
                .filterNotNull()
                .map { ProfileMeUiState.Success(it) as ProfileMeUiState }
                .catch { e -> _profileMeUiState.value = ProfileMeUiState.Error(e.message ?: "Unknown error") }
                .collect { state ->
                    _profileMeUiState.value = state
                    if (state is ProfileMeUiState.Success) {
                        _screenType.value = if (state.data.isStaff) ScreenName.ADMIN.name else ScreenName.MEMBER.name
                    }
                }
        }
    }
}