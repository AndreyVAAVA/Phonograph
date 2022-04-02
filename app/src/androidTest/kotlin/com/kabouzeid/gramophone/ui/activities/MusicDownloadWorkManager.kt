package com.kabouzeid.gramophone.ui.activities

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedInputStream
import java.net.URL

class MusicDownloadWorkManager(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        return Result.success()
    }
    /*fun downloadFile(vararg p0: String?): String {
        val url = URL(p0[0])
        val connection = url.openConnection()
        connection.connect()
        val inputStream = BufferedInputStream(url.openStream())
        val filename = "audio.mp3"
        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val data = ByteArray(1024)
        var total:Long = 0
        var count = 0
        while (inputStream.read(data) != -1) {
            count = inputStream.read(data)
            total += count
            outputStream.write(data, 0, count)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        println("finished saving audio.mp3 to internal storage")
        return "Success"
    }*/
}