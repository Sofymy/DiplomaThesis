package bme.vik.diplomathesis.domain.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.telephony.TelephonyManager
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.PowerConnectionInfo
import bme.vik.diplomathesis.domain.utils.HiltBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PowerConnectionReceiver: BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        try{
            val bundle = intent.extras!!
            val newState = bundle.getString(TelephonyManager.EXTRA_STATE)

            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                context.registerReceiver(null, ifilter)
            }


            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL


            val batteryPct: Float? = batteryStatus?.let { batteryIntent ->
                val level: Int = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                level * 100 / scale.toFloat()
            }

            GlobalScope.launch(Dispatchers.IO) {
                mainRepository.savePowerConnection(
                    PowerConnectionInfo(
                        batteryPct = batteryPct?.toInt(),
                        isCharging = isCharging
                    )
                ) { error ->
                    if (error != null) {
                        println("Error saving power connection: ${error.message}")
                    } else {
                        println("Power connection saved successfully")
                    }
                }
            }

        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

}