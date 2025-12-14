package com.financeadaptative

import android.content.Context
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BackgroundLogWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val message = inputData.getString(KEY_MESSAGE) ?: DEFAULT_MESSAGE
        Log.i(TAG, "WorkManager log: $message")
        showNotification(message)
        return Result.success()
    }

    companion object {
        const val TAG = "BackgroundLogWorker"
        const val KEY_MESSAGE = "message"
        const val DEFAULT_MESSAGE = "Tarea periódica ejecutada"
        const val UNIQUE_NAME = "periodic_log_worker"
        private const val CHANNEL_ID = "bg_log_channel"
        private const val CHANNEL_NAME = "Log en segundo plano"
        private const val NOTIFICATION_ID = 1001
    }

    private fun showNotification(message: String) {
        val nm = NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val sysNm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            sysNm.createNotificationChannel(channel)
        }
        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("WorkManager")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        nm.notify(NOTIFICATION_ID, notif)
    }
}
