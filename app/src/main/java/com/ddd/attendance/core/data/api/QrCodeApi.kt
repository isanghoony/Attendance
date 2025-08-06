package com.ddd.attendance.core.data.api

import com.ddd.attendance.core.data.ApiResponse
import com.ddd.attendance.core.data.api.model.qrcode.QrCodeResponse
import com.ddd.attendance.core.data.api.model.qrcode.QrValidateResponse
import com.ddd.attendance.core.data.api.request.qr.QrValidateRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface QrCodeApi {
    /** QR 코드 생성 API */
    @POST("/api/v1/qrcodes/")
    suspend fun generateQr(): ApiResponse<QrCodeResponse>

    /** QR 코드 검증 API */
    @POST("/api/v1/qrcodes/validate/")
    suspend fun validateQr(@Body requestBody: QrValidateRequest): ApiResponse<QrValidateResponse>
}