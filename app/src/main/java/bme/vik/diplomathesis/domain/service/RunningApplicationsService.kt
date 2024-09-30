package bme.vik.diplomathesis.domain.service

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import bme.vik.diplomathesis.domain.model.running_applications.RunningApplicationInfo
import bme.vik.diplomathesis.domain.model.running_applications.RunningApplicationsHolder
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class RunningApplicationsService : Service() {

    private val runningApplicationsHolder = RunningApplicationsHolder()

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
        ServiceUtils.scheduleTask(5000) { getRunningApp() }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getRunningApp() {
        try {
            val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()

            val appList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 10000 * 10000,
                time
            )
            if (!appList.isNullOrEmpty()) {
                val currentApp = appList.maxByOrNull { it.lastTimeUsed }
                currentApp?.let {
                    getApplicationLabel(it.packageName)?.let { appLabel ->
                        val runningApplicationInfo = RunningApplicationInfo(applicationName = appLabel)
                        handleRunningApplicationsHolder(runningApplicationInfo)

                        scope.launch {
                            mainRepository.saveRunningApplications(runningApplicationsHolder){ error ->
                                if (error != null) {
                                    println("Error saving running apps: ${error.message}")
                                } else {
                                    println("Running apps saved successfully")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleRunningApplicationsHolder(runningApplicationInfo: RunningApplicationInfo) {
        if (runningApplicationsHolder.runningApplications.isEmpty() ||
            runningApplicationsHolder.runningApplications.last().applicationName != runningApplicationInfo.applicationName) {

            runningApplicationsHolder.runningApplications.lastOrNull()?.let {
                it.closeTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
            }
            runningApplicationsHolder.runningApplications.add(runningApplicationInfo)
        }
    }

    private fun getApplicationLabel(packageName: String): String? {
        val packageManager = packageManager
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .firstOrNull { it.packageName == packageName }
            ?.let { packageManager.getApplicationLabel(it).toString() }
    }
}
