package com.ddd.attendance.feature.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.ddd.attendance.core.model.google.GoogleLogin
import com.ddd.attendance.core.ui.theme.AttendanceTheme
import com.ddd.attendance.core.ui.theme.DDD_BLACK
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginProcessActivity : ComponentActivity() {

    companion object {
        private const val TAG = "LoginProcessActivity"
        private const val CLIENT_ID = "369957721624-ajhu5s3msc9jbhsrjgp561lpghaik9ji.apps.googleusercontent.com"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val snackBarMessage = mutableStateOf<String?>(null)

    private val googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleSignInResult(result)
    }

    // 로그인 요청시 바로바로 GoogleLogin 값을 수신하고 화면 전환을 위해서 만든 Callback
    private var onGoogleLoginResult: ((GoogleLogin) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGoogleSignIn()
        initFirebaseAuth()
        setupWindow()

        setContent {
            AttendanceTheme {
                LoginProcessScreen(
                    onClickGoogle = { onResult ->
                        onGoogleLoginResult = onResult // 나중에 로그인 완료되면 호출할 콜백 저장
                        launchGoogleSignIn()
                    },
                    snackBarMessage = snackBarMessage.value,
                    onDismissSnackBar = { snackBarMessage.value = null }
                )
            }
        }
    }

    private fun initGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(CLIENT_ID)
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, options)
    }

    private fun initFirebaseAuth() {
        auth = Firebase.auth
    }

    private fun setupWindow() {
        window.statusBarColor = DDD_BLACK.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }

    private fun launchGoogleSignIn() {
        googleLoginLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleSignInResult(result: ActivityResult) {
        //if (result.resultCode != RESULT_OK) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let {
                handleGoogleIdToken(it)
            }
        } catch (e: ApiException) {
            snackBarMessage.value = "Google Sign-In failed: ${e.message}"
        }
    }

    private fun handleGoogleIdToken(idToken: String) {
        Log.d(TAG, "Google 로그인 요청")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result.user

                    val loginResult = GoogleLogin(
                        email = user?.email.orEmpty(),
                        name = user?.displayName.orEmpty(),
                        uid = user?.uid.orEmpty()
                    )
                    // 여기서 콜백 호출해서 화면에 결과 전달
                    onGoogleLoginResult?.invoke(loginResult)
                    Log.d(TAG, "Google 로그인 성공")
                } else {
                    Log.e(TAG, "Google 로그인 실패")
                }
                Log.d(TAG, "Google 로그인 완료")
            }
    }
}