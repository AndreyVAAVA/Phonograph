package com.kabouzeid.gramophone.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MusicDownloadFileBroadcastChecker: BroadcastReceiver() {

    companion object {
        var isDownloadComplete = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        isDownloadComplete = true
        Log.i("Download completed?", isDownloadComplete.toString())
    }

}