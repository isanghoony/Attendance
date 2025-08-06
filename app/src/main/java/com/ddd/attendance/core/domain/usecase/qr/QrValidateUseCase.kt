package com.ddd.attendance.core.domain.usecase.qr

import com.ddd.attendance.core.data.repository.DefaultQrCodeRepository
import javax.inject.Inject

class QrValidateUseCase @Inject constructor(private val repository: DefaultQrCodeRepository) {
    operator fun invoke(qrString: String) = repository.validateQr(qrString = qrString)
}