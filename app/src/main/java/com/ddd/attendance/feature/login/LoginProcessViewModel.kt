package com.ddd.attendance.feature.login

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.accounts.CheckEmailUseCase
import com.ddd.attendance.core.domain.usecase.accounts.LoginEmailUseCase
import com.ddd.attendance.core.domain.usecase.accounts.RegistrationUseCase
import com.ddd.attendance.core.domain.usecase.invites.ValidateUseCase
import com.ddd.attendance.core.domain.usecase.profiles.GetProfileMeUseCase
import com.ddd.attendance.core.domain.usecase.profiles.PatchProfileMeUseCase
import com.ddd.attendance.core.model.accounts.UserInfo
import com.ddd.attendance.core.model.google.GoogleLogin
import com.ddd.attendance.feature.login.model.CheckEmailUiState
import com.ddd.attendance.feature.login.model.LoginEmailUiState
import com.ddd.attendance.feature.login.model.ProfileMeUiState
import com.ddd.attendance.feature.login.model.RegistrationUiState
import com.ddd.attendance.feature.login.model.ValidateUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val checkEmailUseCase: CheckEmailUseCase,
    private val loginEmailUseCase: LoginEmailUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val validateUseCase: ValidateUseCase,
    private val patchProfileMeUseCase: PatchProfileMeUseCase,
    private val getProfileMeUseCase: GetProfileMeUseCase
) : ViewModel() {
    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    private val _checkEmailState = MutableStateFlow<CheckEmailUiState>(CheckEmailUiState.Loading)
    val checkEmailState = _checkEmailState.asStateFlow()

    private val _loginEmailState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Loading)
    val loginEmailState = _loginEmailState.asStateFlow()

    private val _registrationUiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Loading)
    val registrationUiState: StateFlow<RegistrationUiState> = _registrationUiState.asStateFlow()

    private val _validateUiState = MutableStateFlow<ValidateUiState>(ValidateUiState.Loading)
    val validateUiState: StateFlow<ValidateUiState> = _validateUiState.asStateFlow()

    private val _profileMeUiState = MutableStateFlow<ProfileMeUiState>(ProfileMeUiState.Loading)
    val profileMeUiState: StateFlow<ProfileMeUiState> = _profileMeUiState.asStateFlow()

    fun setUpdateUser(googleLogin: GoogleLogin) {
        _userInfo.value = _userInfo.value.copy(
            email = googleLogin.email,
            name = googleLogin.name,
            uid = googleLogin.uid
        )
        Log.d("구글 로그인 결과", "name: ${googleLogin.name}, email: ${googleLogin.email}, uid: ${googleLogin.uid}")

    }

    private fun updateUserInfo(update: UserInfo.() -> UserInfo, logTag: String) {
        _userInfo.value = _userInfo.value.update()
        Log.d(logTag, "${_userInfo.value}")
    }

    private fun setUpdateUserInviteType(value: String) = updateUserInfo(update = { copy(inviteType = value) }, "유저 타입 업데이트")

    private fun setUpdateUserInviteCodeId(value: String) = updateUserInfo(update = { copy(inviteCodeId = value) }, "유저 초대 코드 ID 업데이트")

    fun setUpdateUserName(value: String) = updateUserInfo(update = { copy(name = value) }, "유저 이름 업데이트")

    fun setUpdateUserRole(value: String) = updateUserInfo(update = { copy(role = value) }, "유저 직무 업데이트")

    fun setUpdateUserTeam(value: String) = updateUserInfo(update = { copy(team = value) }, "유저 팀 업데이트")

    fun setUpdateUserAffiliation(value: String) = updateUserInfo(update = { copy(affiliation = value) }, "유저 소속 업데이트")

    fun checkEmail() {
        // 이메일 체크
        viewModelScope.launch {
            checkEmailUseCase(email = _userInfo.value.email)
                .map { CheckEmailUiState.Success(it) }
                .catch { _errorFlow.emit(it) }
                .collect {
                    if (it.data.emailUsed) loginEmail()
                    else _eventFlow.emit(UiEvent.SignUpEvent)
                }
        }
    }

    private fun loginEmail() {
        //로그인 요청
        viewModelScope.launch {
            loginEmailUseCase(email = _userInfo.value.email)
                .map { LoginEmailUiState.Success(it) }
                .catch { _errorFlow.emit(it) }
                .collect {
                    _eventFlow.emit(UiEvent.GoMianEvent)
                }
        }
    }

    fun registration() {
        //가입 요청
        viewModelScope.launch {
            _registrationUiState.value = RegistrationUiState.Loading

            _userInfo.value.apply {
                registrationUseCase(
                    owner = name,
                    email = email,
                    password1 = uid,
                    password2 = uid
                )
                    .map { RegistrationUiState.Success(it) }
                    .catch { _errorFlow.emit(it) }
                    .collect {
                        patchProfileMe()
                    }
            }
        }
    }

    fun inviteValidation(value: String) {
        //인증 번호 검증
        viewModelScope.launch {
            _validateUiState.value = ValidateUiState.Loading

            validateUseCase(inviteCode = value)
                .map { ValidateUiState.Success(it) }
                .catch { _errorFlow.emit(it) }
                .collect {
                    setUpdateUserInviteType(it.data.inviteType)
                    setUpdateUserInviteCodeId(it.data.inviteCodeId)

                    _eventFlow.emit(UiEvent.NameEvent) // 이름 입력 화면 이동
                }
        }
    }

    private fun patchProfileMe() {
        viewModelScope.launch {
            _profileMeUiState.value = ProfileMeUiState.Loading

            _userInfo.value.apply {
                patchProfileMeUseCase(
                    name = name,
                    inviteCodeId = inviteCodeId,
                    role = role,
                    team = team,
                    responsibility = affiliation,
                    cohort = ""
                )
                    .map { ProfileMeUiState.Success(it) }
                    .catch { _errorFlow.emit(it) }
                    .collect { state ->
                        _profileMeUiState.value = state
                    }
            }
        }
    }
}

sealed class UiEvent {
    data object InvitationEvent: UiEvent()
    data object NameEvent: UiEvent()
    data object JobEvent: UiEvent()
    data object RoleEvent: UiEvent()
    data object TeamEvent: UiEvent()
    data object GoMianEvent : UiEvent()
    data object SignUpEvent: UiEvent()
}

sealed interface UiState {
    data class Success(val googleLogin: GoogleLogin) : UiState
    data class Error(val error: String?) : UiState
    data object Loading : UiState
}