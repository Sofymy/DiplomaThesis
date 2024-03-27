package bme.vik.diplomathesis.model.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.model.data.Cell
import bme.vik.diplomathesis.model.data.Network
import bme.vik.diplomathesis.model.data.networkTypeClass
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class NetworkService(
) : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    //private var keyguardLockedHolder = KeyguardLockedHolder()

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
                getNetworkInfo()
            },/* delay = */ 5000,
            /* period = */ 5000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getNetworkInfo() {
        try {
            val telephonyManager = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
// Requires permission: android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION
            val cellInfo = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            } else {
                telephonyManager.allCellInfo
            }
            val networkOperator = telephonyManager.networkOperatorName
            val networkType = networkTypeClass(telephonyManager.dataNetworkType)
            val networkRoaming = telephonyManager.isNetworkRoaming
            val networkSpecifier = telephonyManager.networkSpecifier

            val networkManualNetworkSelectionAllowed =                telephonyManager.isManualNetworkSelectionAllowed

            val networkSelectionMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                telephonyManager.networkSelectionMode
            } else {
                null
            }


            val network = Network(
                networkOperator,
                networkType,
                networkRoaming,
                networkSelectionMode,
                networkSpecifier,
                networkManualNetworkSelectionAllowed
            )
            mainRepository.saveNetwork(network) {

            }
            val cellLocation = telephonyManager.cellLocation as GsmCellLocation
            val cid = cellLocation.cid
            val lac = cellLocation.lac
            val psc = cellLocation.psc

            val cell = Cell(cid, lac, psc)

            mainRepository.saveCell(cell) {

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
