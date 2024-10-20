package bme.vik.diplomathesis.domain.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.NetworkInfo
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import bme.vik.diplomathesis.domain.utils.networkTypeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NetworkService : Service() {

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
        ServiceUtils.scheduleTask(5000) { getNetworkInfo() }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getNetworkInfo() {
        try {
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

            val networkInfo = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {
                NetworkInfo(
                    networkOperator = telephonyManager.networkOperatorName,
                    networkType = networkTypeClass(telephonyManager.dataNetworkType),
                    networkRoaming = telephonyManager.isNetworkRoaming,
                    networkSelectionMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) telephonyManager.networkSelectionMode else null,
                    networkSpecifier = telephonyManager.networkSpecifier,
                    networkManualNetworkSelectionAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) telephonyManager.isManualNetworkSelectionAllowed else null
                )
            }

            scope.launch {
                mainRepository.saveNetwork(networkInfo){ error ->
                    if (error != null) {
                        println("Error saving network info: ${error.message}")
                    } else {
                        println("Network info saved successfully")
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
