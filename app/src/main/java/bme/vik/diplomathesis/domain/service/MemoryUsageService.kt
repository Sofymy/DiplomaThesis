package bme.vik.diplomathesis.domain.service

import android.app.Service
import android.content.Intent
import android.os.Debug
import android.os.IBinder
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.MemoryUsageInfo
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MemoryUsageService : Service() {

    @Inject
    lateinit var mainRepository: MainRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        ServiceUtils.createNotificationChannel(this)
        ServiceUtils.startForegroundService(this, input)
        ServiceUtils.scheduleTask(5000) { getMemoryUsage() }
        return START_NOT_STICKY
    }

    private fun getMemoryUsage() {
        try {
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory

            //val heapSize = Debug.getNativeHeapSize()
            val allocatedMemory = Debug.getNativeHeapAllocatedSize()
            val freeHeapMemory = Debug.getNativeHeapFreeSize()

            val memoryUsageInfo = MemoryUsageInfo(
                usedMemory,
                allocatedMemory,
                freeHeapMemory
            )

            scope.launch {
                mainRepository.saveMemoryUsage(memoryUsageInfo) { error ->
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

    override fun onDestroy() {
        super.onDestroy()
        ServiceUtils.stopTimer()

        stopForeground(true)
        stopSelf()
    }
}
