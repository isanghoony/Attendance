package com.ddd.attendance.feature.qr

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddd.attendance.core.domain.usecase.qrcode.QrCodeUseCase
import com.ddd.attendance.feature.qr.model.QrCodeUiState
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.EnumMap
import javax.inject.Inject

@HiltViewModel
class QrViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val qrCodeUseCase: QrCodeUseCase
) : ViewModel() {
    val TAG = "QrViewModel"

    private val _isPermissionRequested = MutableStateFlow(false)
    val isPermissionRequested: StateFlow<Boolean> = _isPermissionRequested

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap

    fun setPermissionRequested(value: Boolean) {
        _isPermissionRequested.value = value
    }

    val qrCodeUiState: StateFlow<QrCodeUiState> by lazy {
        qrCodeUseCase()
            .map {
                QrCodeUiState.Success(it.qrString)
            }
            .catch { throwable ->
                //_errorFlow.emit(throwable)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000L),
                QrCodeUiState.Loading
            )
    }

    fun generateQr(qrString: String, qrSize: Int) {
        viewModelScope.launch {
            _qrBitmap.value = generateQRCode(
                qrString = qrString,
                width = qrSize,
                height = qrSize
            )
        }
    }

    private fun generateQRCode(qrString: String, width: Int, height: Int): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        val bitMatrix: BitMatrix =
            MultiFormatWriter()
                .encode(
                    qrString,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
                )

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) {
                    android.graphics.Color.BLACK
                } else {
                    android.graphics.Color.WHITE
                }
            }
        }
        val bitmap = createBitmap(width, height)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        Log.d(TAG,"QR 이미지 값: $qrString")
        return bitmap
    }
}