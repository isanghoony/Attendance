package com.ddd.attendance.core.data.repository

import com.ddd.attendance.core.data.api.QrCodeApi
import com.ddd.attendance.core.data.api.request.qr.QrValidateRequest
import com.ddd.attendance.core.datastore.datasource.AccountPreferencesDataSource
import com.ddd.attendance.core.model.qr.QrCode
import com.ddd.attendance.core.model.qr.QrValidate
import com.ddd.attendance.core.network.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultQrCodeRepository @Inject constructor(
    private val qrCodeApi: QrCodeApi,
    private val dataSource: AccountPreferencesDataSource
): QrCodeRepository {
    override fun generateQR(): Flow<QrCode> = flow {
        emit(
            value = QrCode.from(
                response = qrCodeApi.generateQr().data
            )
        )
    }

    override fun validateQr(qrString: String): Flow<QrValidate> = flow {
        emit(
            value = QrValidate.from(
                response = qrCodeApi.validateQr(
                    QrValidateRequest(qrString = qrString)
                ).data
            )
        )
    }
}