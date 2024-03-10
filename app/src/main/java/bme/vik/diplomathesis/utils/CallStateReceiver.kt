package bme.vik.diplomathesis.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.EXTRA_STATE
import bme.vik.diplomathesis.model.data.callstate.CallStateModel
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallStateReceiver: BroadcastReceiver() {

    private var callModel = CallStateModel()
    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        try{
            val bundle = intent.extras!!
            val newState = bundle.getString(EXTRA_STATE)

            if (newState != null) {
                callModel.phoneAction(newState)
                mainRepository.saveCallState(callModel.getCurrentState()) {

                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

}