package com.ddd.attendance.core.network

import com.ddd.attendance.core.model.qrcode.QrCode
import kotlinx.coroutines.flow.Flow

interface QrCodeRepository {
    fun qrCode(): Flow<QrCode>
}