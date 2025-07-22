package com.ddd.attendance.core.model.qrcode

import com.ddd.attendance.core.data.api.model.qrcode.QrCodeResponse

data class QrCode(
    val id: String,
    val user: Int,
    val qrString: String,
    val createdAt: String,
    val expiresAt: String,
    val decodedAt: String
) {
    companion object {
        fun from(response: QrCodeResponse?): QrCode {
            return QrCode(
                id = response?.id?: "",
                user = response?.user?: 0,
                qrString = response?.qrString?: "",
                createdAt = response?.createdAt?: "",
                expiresAt = response?.expiresAt?: "",
                decodedAt = response?.decodedAt?: ""
            )
        }
    }
}