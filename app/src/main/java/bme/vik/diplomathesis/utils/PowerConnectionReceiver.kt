package bme.vik.diplomathesis.utils

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.telephony.TelephonyManager
import android.util.Log
import bme.vik.diplomathesis.model.data.PowerConnection
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PowerConnectionReceiver: BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

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

            mainRepository.savePowerConnection(
                PowerConnection(batteryPct?.toInt(), isCharging)
            ){
                Log.e(TAG, it?.message.toString())
            }

        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

}