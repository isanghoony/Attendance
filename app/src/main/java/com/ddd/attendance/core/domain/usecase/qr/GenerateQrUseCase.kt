package com.ddd.attendance.core.domain.usecase.qr

import com.ddd.attendance.core.model.qr.QrCode
import com.ddd.attendance.core.network.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateQrUseCase @Inject constructor(private val repository: QrCodeRepository) {
    operator fun invoke(): Flow<QrCode> = repository.generateQR()
}