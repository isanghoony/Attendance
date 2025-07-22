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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState
        .onEach { state ->
            when (state) {
                is UiState.Success -> {
                    setUpdateUser(state.googleLogin)
                    checkEmail()
                }
                else -> {}
            }

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            UiState.Loading
        )

    private val _navigateToInvitation = MutableSharedFlow<Unit>()
    val navigateToInvitation: SharedFlow<Unit> = _navigateToInvitation.asSharedFlow()

    private fun navigateToInvitation() {
        viewModelScope.launch {
            _navigateToInvitation.emit(Unit)
        }
    }

    private val _openMainActivity = MutableSharedFlow<Unit>()
    val openMainActivity: SharedFlow<Unit> = _openMainActivity.asSharedFlow()

    private fun openMainActivity() {
        viewModelScope.launch {
            _openMainActivity.emit(Unit)
        }
    }

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    private val _checkEmailUiState = MutableStateFlow<CheckEmailUiState>(CheckEmailUiState.Empty)
    val checkEmailUiState = _checkEmailUiState
        .onEach { state ->
            when (state) {
                is CheckEmailUiState.Success -> {
                    val isEmailUsed = state.data.emailUsed

                    if (isEmailUsed) { //기존 회원
                        loginEmail()
                    }
                    else { // 신규회원 프로세스
                        navigateToInvitation()
                        resetCheckEmailUiState()
                    }
                }
                else -> {}
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            UiState.Loading
        )

    private val _loginEmailUiState = MutableStateFlow<LoginEmailUiState>(LoginEmailUiState.Empty)
    val loginEmailUiState = _loginEmailUiState
        .onEach { state ->
            when (state) {
                is LoginEmailUiState.Success -> {
                    openMainActivity()
                }
                else -> {}
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            UiState.Loading
        )

    private val _registrationUiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Empty)
    val registrationUiState: StateFlow<RegistrationUiState> = _registrationUiState.asStateFlow()

    private val _validateUiState = MutableStateFlow<ValidateUiState>(ValidateUiState.Empty)
    val validateUiState: StateFlow<ValidateUiState> = _validateUiState.asStateFlow()

    private val _profileMeUiState = MutableStateFlow<ProfileMeUiState>(ProfileMeUiState.Empty)
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
        viewModelScope.launch {
            try {
                _checkEmailUiState.value = CheckEmailUiState.Loading

                val userInfo = _userInfo.value

                checkEmailUseCase(
                    email = userInfo.email
                )
                    .filterNotNull()
                    .map { CheckEmailUiState.Success(it) as CheckEmailUiState }
                    .catch { emit(CheckEmailUiState.Error(it.message ?: "Unknown Error")) }
                    .collect { state ->
                        _checkEmailUiState.value = state
                    }

            } catch (e: Exception) {
                _checkEmailUiState.value = CheckEmailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loginEmail() {
        viewModelScope.launch {
            try {
                _loginEmailUiState.value = LoginEmailUiState.Loading

                val userInfo = _userInfo.value

                loginEmailUseCase(
                    email = userInfo.email
                ).filterNotNull()
                    .map { LoginEmailUiState.Success(it) as LoginEmailUiState }
                    .catch { emit(LoginEmailUiState.Error(it.message ?: "Unknown Error")) }
                    .collect { state ->
                        _loginEmailUiState.value = state
                    }
            } catch (e: Exception) {
                _loginEmailUiState.value = LoginEmailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun registration() {
        viewModelScope.launch {
            try {
                _registrationUiState.value = RegistrationUiState.Loading

                val userInfo = _userInfo.value

                registrationUseCase(
                    owner = userInfo.name,
                    email = userInfo.email,
                    password1 = userInfo.uid,
                    password2 = userInfo.uid
                )
                    .filterNotNull()
                    .map { RegistrationUiState.Success(it) as RegistrationUiState }
                    .catch { emit(RegistrationUiState.Error(it.message ?: "Unknown Error")) }
                    .collect { state ->
                        _registrationUiState.value = state

                        if (state is RegistrationUiState.Success) {
                            patchProfileMe()
                        }
                    }
            } catch (e: Exception) {
                _registrationUiState.value = RegistrationUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun inviteValidation(value: String) {
        viewModelScope.launch {
            try {
                validateUseCase(inviteCode = value)
                    .filterNotNull()
                    .map { ValidateUiState.Success(it) as ValidateUiState }
                    .catch { emit(ValidateUiState.Error(it.message ?: "Unknown Error")) }
                    .collect { state ->
                        _validateUiState.value = state

                        if (state is ValidateUiState.Success) {
                            setUpdateUserInviteType(state.data.inviteType)
                            setUpdateUserInviteCodeId(state.data.inviteCodeId)
                        }
                    }
            } catch (e: Exception) {
                _validateUiState.value = ValidateUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetValidateState() {
        _validateUiState.value = ValidateUiState.Empty
    }

    fun resetCheckEmailUiState() {
        _checkEmailUiState.value = CheckEmailUiState.Empty
    }

    private fun patchProfileMe() {
        viewModelScope.launch {
            try {
                patchProfileMeUseCase(
                    name = _userInfo.value.name,
                    inviteCodeId = _userInfo.value.inviteCodeId,
                    role = _userInfo.value.role,
                    team = _userInfo.value.team,
                    responsibility = _userInfo.value.affiliation,
                    cohort = ""
                )
                    .map { ProfileMeUiState.Success(it) as ProfileMeUiState }
                    .catch { emit(ProfileMeUiState.Error(it.message ?: "Unknown Error")) }
                    .collect { state ->
                        _profileMeUiState.value = state
                    }
            } catch (e: Exception) {
                _profileMeUiState.value = ProfileMeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setUiState(uiState: UiState) {
        viewModelScope.launch {
            _uiState.emit(uiState)
        }
    }
}

sealed interface UiState {
    data class Success(val googleLogin: GoogleLogin) : UiState
    data class Error(val error: String?) : UiState
    data object Loading : UiState
}