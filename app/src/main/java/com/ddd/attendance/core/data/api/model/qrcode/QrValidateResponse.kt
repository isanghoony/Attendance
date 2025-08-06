package com.ddd.attendance.core.data.api.model.qrcode

import com.google.gson.annotations.SerializedName

data class QrValidateResponse(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val userName: String
)