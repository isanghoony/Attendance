package com.ddd.attendance.feature.qr.model

sealed interface QrCodeUiState {
    data object Loading : QrCodeUiState
    data class Success(val qrString: String) : QrCodeUiState
    data class Error(val message: String) : QrCodeUiState
}
