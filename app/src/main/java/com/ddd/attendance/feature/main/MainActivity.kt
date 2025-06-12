package com.ddd.attendance.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.WindowCompat
import com.ddd.attendance.core.ui.theme.AttendanceTheme
import com.ddd.attendance.core.ui.theme.DDD_BLACK
import com.ddd.attendance.feature.login.LoginProcessActivity
import com.ddd.attendance.feature.main.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = DDD_BLACK.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            AttendanceTheme {
                MainScreen(
                    goLogin = {
                        val options = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                        startActivity(Intent(this, LoginProcessActivity::class.java), options.toBundle())
                    }
                )
            }
        }
    }
}