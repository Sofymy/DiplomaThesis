package bme.vik.diplomathesis.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Debug
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.model.data.MemoryUsage
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class MemoryUsageService(
) : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"

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
                getMemoryUsage()
            },/* delay = */ 5000,
            /* period = */ 5000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getMemoryUsage() {
        try {
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory

            val heapSize = Debug.getNativeHeapSize()
            val allocatedMemory = Debug.getNativeHeapAllocatedSize()
            val freeHeapMemory = Debug.getNativeHeapFreeSize()

            val memoryUsage = MemoryUsage(
                (usedMemory),
                (allocatedMemory),
                (freeHeapMemory),
            )
            mainRepository.saveMemoryUsage(memoryUsage) {

            }

        } catch (e: Exception) {
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