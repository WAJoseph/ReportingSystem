package com.example.josephwanis.reportingsystem.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

object BitmapUtil {
    // Function to decode a sampled Bitmap from InputStream to avoid out-of-memory issues
    fun decodeSampledBitmapFromStream(inputStream: InputStream, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()

        // Set inJustDecodeBounds to true to calculate the dimensions of the Bitmap without loading it into memory
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)

        // Calculate the sample size
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Set inJustDecodeBounds to false to actually decode the Bitmap
        options.inJustDecodeBounds = false

        // Decode the Bitmap with the calculated sample size
        return BitmapFactory.decodeStream(inputStream, null, options)
            ?: throw RuntimeException("Failed to decode bitmap from stream.")
    }

    // Function to calculate the sample size based on the required width and height
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}