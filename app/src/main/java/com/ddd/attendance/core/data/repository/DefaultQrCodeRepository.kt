package com.ddd.attendance.core.data.repository

import com.ddd.attendance.core.data.api.QrCodeApi
import com.ddd.attendance.core.datastore.datasource.AccountPreferencesDataSource
import com.ddd.attendance.core.model.qrcode.QrCode
import com.ddd.attendance.core.network.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultQrCodeRepository @Inject constructor(
    private val qrCodeApi: QrCodeApi,
    private val dataSource: AccountPreferencesDataSource
): QrCodeRepository {
    override fun qrCode(): Flow<QrCode> = flow {
        val response = qrCodeApi.qrCode()
        emit(QrCode.from(response.data))
    }
}