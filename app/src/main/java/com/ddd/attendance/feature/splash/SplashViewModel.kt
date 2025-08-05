package com.ddd.attendance.feature.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.accounts.GetAccessTokenUseCase
import com.ddd.attendance.core.domain.usecase.accounts.GetEmailUseCase
import com.ddd.attendance.core.domain.usecase.accounts.LoginEmailUseCase
import com.ddd.attendance.core.domain.usecase.profiles.GetProfileMeUseCase
import com.ddd.attendance.feature.main.screen.ScreenName
import com.ddd.attendance.feature.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getEmailUseCase: GetEmailUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val loginEmailUseCase: LoginEmailUseCase,
    private val getProfileMeUseCase: GetProfileMeUseCase
) : ViewModel() {
    private val TAG = "SplashViewModel"

    private val _uiState = MutableStateFlow(SplashState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        loadCredentials()
        observeAutoLogin()
    }

    /**
     * DataStore 에서 email/token을 읽어 초기 상태 설정
     * DefaultAccountsRepository 통신 성공시 저장
     * */
    private fun loadCredentials() =
        viewModelScope.launch {
            combine(
                getEmailUseCase(),
                getAccessTokenUseCase()
            ) { email, token -> email to token }
                .catch { _errorFlow.emit(it) }
                .firstOrNull()
                ?.let { (email, token) ->
                    if (email.isBlank() || token.isBlank()) {
                        _uiState.update { it.copy(screenName = ScreenName.LOGIN.name) }
                        Log.d(TAG, "로그인 - 수동 로그인 시도")
                    } else {
                        Log.d(TAG, "로그인 - 자동 로그인 시도")
                        _uiState.update { it.copy(email = email, token = token) }
                    }
                }
        }

    // email/token이 채워지면 자동 로그인 시도
    private fun observeAutoLogin() =
        viewModelScope.launch {
            _uiState
                .map { it.email to it.token }
                .distinctUntilChanged() // 동일한 값 반복 방출 방지
                .filter { (email, token) -> email.isNotBlank() || token.isNotBlank() }
                .collectLatest { (email, _) ->
                    _uiState.update { it.copy(isLoading = true) }

                    // 로그인 성공 여부만 필요 → 성공 시 true, 실패 시 catch로 에러 처리
                    loginEmailUseCase(email)
                        .map { true }
                        .catch { _errorFlow.emit(it) }
                        .firstOrNull()
                        ?.let {
                            // 로그인 성공 시 프로필 조회
                            getProfileMeUseCase()
                                .catch { _errorFlow.emit(it) }
                                .firstOrNull()
                                ?.let { profile ->
                                    _uiState.update { s ->
                                        s.copy(
                                            isLoading = false,
                                            isStaff = profile.isStaff,
                                            screenName = if (profile.isStaff) ScreenName.ADMIN.name else ScreenName.MEMBER.name
                                        )
                                    }
                                    Log.d(TAG, "로그인 - 자동 로그인 성공")
                                }
                        }
                }
        }
}