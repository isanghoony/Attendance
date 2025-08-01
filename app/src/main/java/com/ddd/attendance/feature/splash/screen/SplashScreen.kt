package com.ddd.attendance.feature.splash.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ddd.attendance.R
import com.ddd.attendance.core.ui.theme.DDD_WHITE
import com.ddd.attendance.feature.splash.SplashViewModel

@Composable
fun SplashScreen(
    goNext: (screenName: String) -> Unit
) {
    val viewModel: SplashViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val animState by animateLottieCompositionAsState(composition = composition, iterations = 1)

    LaunchedEffect(animState) {
        if (animState == 1f ) {
            goNext(uiState.screenName)
        }
    }

    Content(composition)
}

@Composable
private fun Content(composition: LottieComposition?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DDD_WHITE), // 배경색은 필요 시 변경
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(composition = composition)
        }
    }
}