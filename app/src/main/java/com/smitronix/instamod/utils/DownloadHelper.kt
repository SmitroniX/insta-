package com.smitronix.instamod.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.File

object DownloadHelper {

    fun downloadMedia(context: Context, url: String, type: String = "Reel") {
        try {
            val extension = if (url.contains(".mp4") || url.contains("video")) "mp4" else "jpg"
            val fileName = "Insta_${type}_${System.currentTimeMillis()}.$extension"

            val targetDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Instagram"
            )
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle("Instagram $type")
                setDescription("Downloading $fileName")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "Instagram/$fileName"
                )
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(context, "📥 Download started: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
