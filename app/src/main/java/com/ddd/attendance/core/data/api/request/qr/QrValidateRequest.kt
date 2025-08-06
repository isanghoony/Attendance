package com.ddd.attendance.core.data.api.request.qr

import com.google.gson.annotations.SerializedName

data class QrValidateRequest(
    @SerializedName("qr_string") val qrString: String
)