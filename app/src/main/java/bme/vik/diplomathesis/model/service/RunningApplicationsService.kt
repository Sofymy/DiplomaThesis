package bme.vik.diplomathesis.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.model.data.RunningApplication
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SortedMap
import java.util.Timer
import java.util.TreeMap
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class RunningApplicationsService: Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    private val runningApplicationsHolder = RunningApplicationsHolder()

    @Inject
    lateinit var mainRepository: MainRepository

    @RequiresApi(Build.VERSION_CODES.Q)
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
                getRunningApp()
            },/* delay = */ 5000,
            /* period = */ 5000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getRunningApp() {
        try {
            val usageStatsManager = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()

            val appList = usageStatsManager.queryUsageStats(
                /* intervalType = */ UsageStatsManager.INTERVAL_DAILY,
                /* beginTime = */ time - 10000 * 10000,
                /* endTime = */ time
            )
            if (!appList.isNullOrEmpty()) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap.isNotEmpty()) {
                    val currentApp = mySortedMap[mySortedMap.lastKey()]!!
                    getApplicationLabel(
                        context = this,
                        packageName = currentApp.packageName
                    )?.let {
                        val runningApplication = RunningApplication(
                            applicationName = it
                        )
                        handleRunningApplicationsHolder(runningApplication = runningApplication)
                        mainRepository.saveRunningApplications(runningApplicationsHolder) {

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleRunningApplicationsHolder(runningApplication: RunningApplication) {
        if (runningApplicationsHolder.runningApplications.isEmpty() or
            (runningApplicationsHolder.runningApplications.lastOrNull()?.applicationName
                    != runningApplication.applicationName)){

            if(runningApplicationsHolder.runningApplications.isNotEmpty()){
                runningApplicationsHolder.runningApplications.last().closeTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(
                    Date()
                )
            }
            runningApplicationsHolder.runningApplications.add(runningApplication)
        }
    }


    private fun getApplicationLabel(context: Context, packageName: String): String? {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        var label: String? = null
        for (i in packages.indices) {
            val temp = packages[i]
            if (temp.packageName == packageName) label =
                packageManager.getApplicationLabel(temp).toString()
        }
        return label
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