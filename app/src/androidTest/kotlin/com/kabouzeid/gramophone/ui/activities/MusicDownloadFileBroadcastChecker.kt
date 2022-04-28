package com.kabouzeid.gramophone.ui.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MusicDownloadFileBroadcastChecker : BroadcastReceiver() {

    companion object {
        var isDownloadComplete = false
        var downloadId: Long? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId != null) {
            isDownloadComplete = if (id == downloadId) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE == action
            } else {
                false
            }
        }
    }

}