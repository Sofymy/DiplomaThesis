package bme.vik.diplomathesis.domain.service

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.StorageUsageInfo
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class StorageUsageService : Service() {

    @Inject
    lateinit var mainRepository: MainRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        ServiceUtils.createNotificationChannel(this)
        ServiceUtils.startForegroundService(this, input)
        ServiceUtils.scheduleTask(5000) { getStorageUsage() }
        return START_NOT_STICKY
    }

    private fun getStorageUsage() {
        try {
            val externalStorageDirectory = Environment.getExternalStorageDirectory()
            val totalSpace = externalStorageDirectory.totalSpace
            val freeSpace = externalStorageDirectory.freeSpace

            val storageUsageInfo = StorageUsageInfo(totalSpace, freeSpace)

            scope.launch {
                mainRepository.saveStorageUsage(storageUsageInfo){ error ->
                    if (error != null) {
                        println("Error saving storage usage: ${error.message}")
                    } else {
                        println("Storage usage saved successfully")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceUtils.stopTimer()

        stopForeground(true)
        stopSelf()
    }
}
