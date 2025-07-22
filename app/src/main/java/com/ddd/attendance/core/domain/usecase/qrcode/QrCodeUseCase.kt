package com.ddd.attendance.core.domain.usecase.qrcode

import com.ddd.attendance.core.model.qrcode.QrCode
import com.ddd.attendance.core.network.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QrCodeUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    operator fun invoke(): Flow<QrCode> = repository.qrCode()
}