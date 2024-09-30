package bme.vik.diplomathesis.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.domain.model.MobileTrafficInfo
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class MobileTrafficBytesService : Service() {

    private var mobileTrafficInfo = MobileTrafficInfo()

    @Inject
    lateinit var mainRepository: MainRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        ServiceUtils.createNotificationChannel(this)
        ServiceUtils.startForegroundService(this, input)
        ServiceUtils.scheduleTask(5000) { getTrafficStats() }
        return START_NOT_STICKY
    }

    private fun getTrafficStats() {
        try {
            val totalReceiveCount = TrafficStats.getMobileRxBytes()
            val totalTransmitCount = TrafficStats.getMobileTxBytes()
            mobileTrafficInfo = MobileTrafficInfo(totalReceiveCount, totalTransmitCount)

            scope.launch {
                mainRepository.saveMobileTrafficBytes(mobileTrafficInfo){ error ->
                    if (error != null) {
                        println("Error saving memory usage: ${error.message}")
                    } else {
                        println("Memory usage saved successfully")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
