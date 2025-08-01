package com.ddd.attendance.feature.qr.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ddd.attendance.core.designsystem.DDDText
import com.ddd.attendance.core.designsystem.DDDTopBar
import com.ddd.attendance.core.designsystem.TopBarType
import com.ddd.attendance.core.ui.theme.DDD_BLACK
import com.ddd.attendance.core.ui.theme.DDD_WHITE
import com.ddd.attendance.feature.qr.QrViewModel
import com.ddd.attendance.feature.qr.model.QrCodeUiState

@Composable
fun QrImageScreen(
    navController: NavController,
    viewModel: QrViewModel = hiltViewModel()
) {
    val qrCodeUiState = viewModel.qrCodeUiState.collectAsStateWithLifecycle()
    val qrBitmap by viewModel.qrBitmap.collectAsStateWithLifecycle()

    val screenWidthInPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx().toInt()
    }

    LaunchedEffect(qrCodeUiState.value) {
        if (qrCodeUiState.value is QrCodeUiState.Success) {
            val qrString = (qrCodeUiState.value as QrCodeUiState.Success).qrString
            viewModel.generateQr(qrString = qrString, qrSize = screenWidthInPx)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(DDD_BLACK)
    ) {
        DDDTopBar(
            type = TopBarType.LEFT_IMAGE,
            onClickLeftImage = {
                navController.popBackStack()
            },
        )
        Content(qrImage = qrBitmap)
    }
}

@Composable
private fun Content(
    qrImage: Bitmap?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DDDText(
            text = "QR 코드를 스캔해 주세요.",
            color = DDD_WHITE,
            textStyle = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        DDDText(
            text = "스캔 시 자동으로 출석이 인정됩니다.",
            color = DDD_WHITE,
            textStyle = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        qrImage?.let {
            Image(
                modifier = Modifier.padding(horizontal = 32.dp),
                bitmap = it.asImageBitmap(),
                contentDescription = "qr image"
            )
        }
    }
}