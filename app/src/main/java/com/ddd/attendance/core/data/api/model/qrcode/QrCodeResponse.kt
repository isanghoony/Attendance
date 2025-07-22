package com.ddd.attendance.core.data.api.model.qrcode

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class QrCodeResponse(
    @SerializedName("qr_string") val qrString: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("decoded_at") val decodedAt: String,
    val id: String,
    val user: Int
)