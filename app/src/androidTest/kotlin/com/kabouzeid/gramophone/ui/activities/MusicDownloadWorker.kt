package com.kabouzeid.gramophone.ui.activities

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL

class MusicDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = URL(inputData.getString(DownloadWorkerArgumentsIDs.LINK_ID.toString()))
        val filename = inputData.getString(DownloadWorkerArgumentsIDs.SONG_NAME.toString())
        val connection = url.openConnection()
        connection.connect()
        val inputStream = BufferedInputStream(url.openStream())
        withContext(Dispatchers.IO) {

            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .toString() + "/" + filename
            )
            val writer = FileOutputStream(file)
            var buff = ByteArray(1024)
            while (inputStream.available() > 0) {
                inputStream.read(buff)
                writer.write(buff)
            }
            /*val outputStream = this.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE)
        val data = ByteArray(1024)
        var count = inputStream.read(data)
        var total = count
        while (count != -1) {
            outputStream.write(data, 0, count)
            count = inputStream.read(data)
            total += count
        }
        outputStream.close()*/
            writer.close()
            inputStream.close()
        }
        println("finished saving $filename to internal storage")
        return Result.success()
    }
}