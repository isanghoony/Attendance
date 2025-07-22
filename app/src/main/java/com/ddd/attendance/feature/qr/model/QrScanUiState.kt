package com.ddd.attendance.feature.qr.model

sealed interface QrScanUiState {
    data object Loading : QrScanUiState
    data class Success(val userId: String, val timeStamp: String) : QrScanUiState
    data class Error(val message: String) : QrScanUiState
}
