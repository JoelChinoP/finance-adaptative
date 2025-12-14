package com.financeadaptative

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.financeadaptative.ui.theme.FinPerTheme

class MainActivity : ComponentActivity() {
    private var startTimerAfterPermission: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinPerTheme(darkTheme = true, dynamicColor = false) {
                FinPerApp()
            }
        }
    }

    fun requestNotifPermissionAndStartTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                startTimerAfterPermission = true
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQ_NOTIF)
                return
            }
        }
        TimerService.start(this)
    }

    fun debugPostNotification() {
        // Publicar una notificación simple para verificar permisos/canales
        val nm = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = android.app.NotificationChannel(
                "debug_channel",
                "Debug Notifs",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(chan)
        }
        val notif = androidx.core.app.NotificationCompat.Builder(this, "debug_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Prueba de notificación")
            .setContentText("Si ves esto, las notificaciones funcionan")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(999, notif)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_NOTIF) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted && startTimerAfterPermission) {
                startTimerAfterPermission = false
                TimerService.start(this)
            }
        }
    }

    companion object {
        private const val REQ_NOTIF = 1005
    }
}