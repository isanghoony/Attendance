package com.ddd.attendance.core.data.api

import com.ddd.attendance.core.data.ApiResponse
import com.ddd.attendance.core.data.api.model.qrcode.QrCodeResponse
import retrofit2.http.POST

interface QrCodeApi {
    /** QR 코드 생성 API */
    @POST("/api/v1/qrcodes/")
    suspend fun qrCode(): ApiResponse<QrCodeResponse>
}