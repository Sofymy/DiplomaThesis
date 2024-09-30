package bme.vik.diplomathesis.domain.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import java.util.Timer
import kotlin.concurrent.timerTask

object ServiceUtils {
    private const val CHANNEL_ID = "ForegroundService Kotlin"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground RunningApplicationsService Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    fun startForegroundService(service: Service, input: String?) {
        val notificationIntent = Intent(service, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            service,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(service, CHANNEL_ID)
            .setContentTitle("Foreground RunningApplicationsService")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .build()

        service.startForeground(1, notification)
    }

    fun scheduleTask(interval: Long, task: () -> Unit): Timer {
        val timer = Timer()
        timer.schedule(
            timerTask { task() },
            interval,
            interval
        )
        return timer
    }
}