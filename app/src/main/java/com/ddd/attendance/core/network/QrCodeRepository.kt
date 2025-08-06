package com.ddd.attendance.core.network

import com.ddd.attendance.core.model.qr.QrCode
import com.ddd.attendance.core.model.qr.QrValidate
import kotlinx.coroutines.flow.Flow

interface QrCodeRepository {
    fun generateQR(): Flow<QrCode>
    fun validateQr(qrString: String): Flow<QrValidate>
}