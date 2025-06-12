package com.ddd.attendance.feature.splash

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.invites.GetInviteTypeUseCase
import com.ddd.attendance.feature.main.screen.ScreenName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val inviteTypeUseCase: GetInviteTypeUseCase
) : ViewModel() {
    private val _startDestination = MutableStateFlow("")
    val startDestination: StateFlow<String> = _startDestination

    init {
        viewModelScope.launch {
            inviteTypeUseCase().collect {
                val loginType = when (it) {
                    "member" -> ScreenName.MEMBER.name
                    "moderator" -> ScreenName.ADMIN.name
                    else -> ScreenName.LOGIN.name
                }
                _startDestination.value = loginType
            }
        }
    }
}