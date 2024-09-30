package bme.vik.diplomathesis.domain.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.CellInfo
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CellService : Service() {

    @Inject
    lateinit var mainRepository: MainRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ServiceUtils.createNotificationChannel(this)
        ServiceUtils.startForegroundService(this, null)
        ServiceUtils.scheduleTask(5000) { getCellInfo() }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCellInfo() {

        try {
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val cellLocation = if (ActivityCompat.checkSelfPermission(
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
                telephonyManager.cellLocation as GsmCellLocation
            }

            val cellInfo = CellInfo(
                cid = cellLocation.cid,
                lac = cellLocation.lac,
                psc = cellLocation.psc
            )

            scope.launch {
                mainRepository.saveCell(cellInfo){ error ->
                    if (error != null) {
                        println("Error saving cell info: ${error.message}")
                    } else {
                        println("Cell info saved successfully")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
