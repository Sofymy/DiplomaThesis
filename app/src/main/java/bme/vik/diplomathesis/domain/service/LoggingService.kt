package bme.vik.diplomathesis.domain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LoggingService : Service() {

    private lateinit var loggingJob: Job

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        loggingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(5000)
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "logging_channel"
        val channelName = "Logging Service"
        val channelImportance = NotificationManager.IMPORTANCE_LOW

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, channelImportance)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Logging Service")
            .setContentText("Logging is running in the background.")
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        loggingJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
