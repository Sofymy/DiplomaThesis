package bme.vik.diplomathesis.domain.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.EXTRA_STATE
import android.widget.Toast
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.model.call_state.CallStateModel
import bme.vik.diplomathesis.domain.utils.HiltBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallStateReceiver : BroadcastReceiver() {

    private val callModel = CallStateModel()

    @Inject
    lateinit var repository: MainRepository

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        intent.extras?.getString(EXTRA_STATE)?.let { newState ->
            Toast.makeText(context, newState, Toast.LENGTH_LONG).show()
            callModel.phoneAction(newState)

            GlobalScope.launch(Dispatchers.IO) {
                repository.saveCallState(callModel.getCurrentState()) { error ->
                    if (error != null) {
                        println("Error saving call state: ${error.message}")
                    } else {
                        println("Call state saved successfully")
                    }
                }
            }
        }
    }
}
