package com.ddd.attendance.feature.splash.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ddd.attendance.R
import com.ddd.attendance.feature.main.screen.ScreenName
import com.ddd.attendance.feature.splash.SplashViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
    goLogin:() -> Unit
) {
    val startDestination by viewModel.startDestination.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val animState by animateLottieCompositionAsState(composition = composition, iterations = 1)

    LaunchedEffect(animState) {
        if (animState == 1F) {
            if (startDestination == ScreenName.LOGIN.name) goLogin()
            else navController.navigate(route = startDestination)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animState },
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}