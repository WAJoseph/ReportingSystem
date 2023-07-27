package com.example.josephwanis.reportingsystem.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException

object ImageUtil {
    // Function to convert Bitmap to ByteArray
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    // Function to get Bitmap from Uri
    @Throws(IOException::class)
    fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(imageUri)?.use {
            BitmapUtil.decodeSampledBitmapFromStream(it, 100, 100)
        }
    }
}