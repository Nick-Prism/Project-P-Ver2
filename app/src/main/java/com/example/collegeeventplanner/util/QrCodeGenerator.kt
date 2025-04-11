package com.example.collegeeventplanner.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.EnumMap

class QrCodeGenerator {

    companion object {
        private const val QR_CODE_SIZE = 512
        private const val BLACK = 0xFF000000.toInt()
        private const val WHITE = 0xFFFFFFFF.toInt()
    }

    fun generateQrCodeBitmap(
        content: String,
        size: Int = QR_CODE_SIZE,
        foregroundColor: Int = BLACK,
        backgroundColor: Int = WHITE
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.MARGIN, 1) // Set margin to 1 (minimum)
        }

        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )
        } catch (e: Exception) {
            throw RuntimeException("Error generating QR code", e)
        }

        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
            }
        }

        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }

    fun generateQrCodeWithLogo(
        content: String,
        logo: Bitmap,
        size: Int = QR_CODE_SIZE
    ): Bitmap {
        val qrCode = generateQrCodeBitmap(content, size)
        
        // Calculate position to center the logo
        val left = (size - logo.width) / 2
        val top = (size - logo.height) / 2
        
        // Create a new bitmap with logo overlay
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            val canvas = android.graphics.Canvas(this)
            canvas.drawBitmap(qrCode, 0f, 0f, null)
            canvas.drawBitmap(logo, left.toFloat(), top.toFloat(), null)
        }
    }
}