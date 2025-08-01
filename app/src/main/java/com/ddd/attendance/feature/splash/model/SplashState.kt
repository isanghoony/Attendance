package com.ddd.attendance.feature.splash.model

import androidx.compose.runtime.Immutable
import com.ddd.attendance.feature.main.screen.ScreenName

@Immutable
data class SplashState(
    val isLoading: Boolean = false,
    val email: String = "",
    val token: String = "",
    val screenName: String = ScreenName.NONE.name,
    val isStaff: Boolean = false
)

sealed interface SplashSideEffect {
    data object Loading : SplashSideEffect
    data class AutoLogin(val email: String, val token: String) : SplashSideEffect
}