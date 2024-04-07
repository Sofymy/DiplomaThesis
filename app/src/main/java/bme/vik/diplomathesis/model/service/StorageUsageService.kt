package bme.vik.diplomathesis.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.model.data.StorageUsage
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class StorageUsageService(
) : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    //private var keyguardLockedHolder = KeyguardLockedHolder()

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let {
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground RunningApplicationsService")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        val timer = Timer()
        timer.scheduleAtFixedRate(
            /* task = */ timerTask()
            {
                getStorageUsage()
            },/* delay = */ 5000,
            /* period = */ 5000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getStorageUsage() {
        try {
            val externalStorageDirectory = Environment.getExternalStorageDirectory()
            val totalSpace = externalStorageDirectory.totalSpace
            val freeSpace = externalStorageDirectory.freeSpace


            val storageUsage = StorageUsage(
                (totalSpace),
                (freeSpace)
            )
            mainRepository.saveStorageUsage(storageUsage) {

            }
        } catch (e: Exception) {
            Log.d("eeeeee", e.message.toString())
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground RunningApplicationsService Channel",
                NotificationManager.IMPORTANCE_DEFAULT)

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}