package com.example.josephwanis.reportingsystem.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

object VideoUtil {
    // Function to get a thumbnail from a video file
    fun getVideoThumbnail(context: Context, videoFileUri: Uri, targetWidth: Int, targetHeight: Int): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            mediaMetadataRetriever.setDataSource(context, videoFileUri)
            val frameAtTime = mediaMetadataRetriever.getFrameAtTime(-1)

            // Handle the case where the frameAtTime is null
            frameAtTime?.let {
                // Resize the thumbnail to the desired dimensions
                Bitmap.createScaledBitmap(frameAtTime, targetWidth, targetHeight, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            mediaMetadataRetriever.release()
        }
    }
}