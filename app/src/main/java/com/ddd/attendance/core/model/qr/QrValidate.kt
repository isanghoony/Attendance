package com.ddd.attendance.core.model.qr

import com.ddd.attendance.core.data.api.model.qrcode.QrValidateResponse

data class QrValidate(
    val valid: Boolean,
    val userId: Int,
    val userName: String
) {
    companion object {
        fun from(response: QrValidateResponse?): QrValidate {
            return QrValidate(
                valid = response?.valid?: false,
                userId = response?.userId?: 0,
                userName = response?.userName?: ""
            )
        }
    }
}