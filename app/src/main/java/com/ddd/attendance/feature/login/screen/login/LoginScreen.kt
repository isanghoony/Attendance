package com.ddd.attendance.feature.login.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ddd.attendance.R
import com.ddd.attendance.core.designsystem.DDDText
import com.ddd.attendance.core.model.google.GoogleLogin
import com.ddd.attendance.core.ui.theme.DDD_300
import com.ddd.attendance.core.ui.theme.DDD_BLACK
import com.ddd.attendance.feature.login.LoginProcessViewModel
import com.ddd.attendance.feature.login.ScreenName
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginProcessViewModel,
    snackBarMessage: String?,
    onDismissSnackBar: () -> Unit,
    onClickGoogle: (result: (GoogleLogin) -> Unit) -> Unit
) {
    val context = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let {
            if (it.isNotBlank()) {
                scope.launch {
                    snackBarHostState.showSnackbar(snackBarMessage)
                    onDismissSnackBar()
                }
            }
        }
    }

    Content(
        snackBarHostState = snackBarHostState,
        onClickGoogle = { // 로그인 성공 결과
            onClickGoogle { result ->
                viewModel.setUpdateUser(result) // 유저 정보 저장
                navController.navigate(route = ScreenName.INVITATION_CODE.name) // 초대 코드 입력 화면으로 전환
            }
        }
    )
}

@Composable
private fun Content(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClickGoogle: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = DDD_BLACK)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_logo),
                    contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
                DDDText(
                    text = "Google로 계속하기",
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .clickable(onClick = onClickGoogle),
                    color = DDD_300,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp
                )
            }
        }
    )

}

@Preview(name = "Content")
@Composable
private fun P1() {
    Content {}
}