package com.example.spotechnify.Music.Musicviewmodel.Util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

// Put this function in the same file or your ViewModel/Util:
fun downloadFile(context: Context, url: String, fileName: String) {
    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle("Downloading $fileName")
        setDescription("Please wait...")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}