package com.ddd.attendance.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ddd.attendance.feature.admin.screen.AddScheduleScreen
import com.ddd.attendance.feature.admin.screen.AdminScreen
import com.ddd.attendance.feature.main.MainViewModel
import com.ddd.attendance.feature.member.screen.MemberScreen
import com.ddd.attendance.feature.mypage.screen.MyPageScreen
import com.ddd.attendance.feature.qr.screen.QrImageScreen
import com.ddd.attendance.feature.qr.screen.QrScanScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()

    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    if (startDestination.isNotBlank()) {
        Column {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(route = ScreenName.MEMBER.name) {
                    MemberScreen(
                        userName = userName,
                        navController = navController
                    )
                }
                composable(route = ScreenName.QR_IMAGE.name) {
                    QrImageScreen(
                        navController = navController
                    )
                }

                composable(route = ScreenName.ADMIN.name) {
                    AdminScreen(
                        navController = navController
                    )
                }

                composable(route = ScreenName.QR_SCAN.name) {
                    QrScanScreen(
                        navController = navController
                    )
                }

                composable(route = ScreenName.MY_PAGE.name) {
                    MyPageScreen(
                        navController = navController
                    )
                }

                composable(route = ScreenName.ADD_SCHEDULE.name) {
                    AddScheduleScreen(
                        navController = navController
                    )
                }

                composable(route = ScreenName.NONE.name) {}
            }
        }
    }
}

enum class ScreenName {
    LOGIN, MEMBER, ADMIN, NONE, QR_IMAGE, QR_SCAN, MY_PAGE, ADD_SCHEDULE
}