package bme.vik.diplomathesis.model.data.callstate

import android.telephony.TelephonyManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallStateModel  {

    private fun changeState(state: CallState) {
        currentState.state = state
        currentState.timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
    }

    fun getCurrentState(): CallStateHolder {
        return currentState
    }

    fun phoneAction(newState: String){
        when(currentState.state){
            CallState.Idle -> {
                //incoming call rings
                if(newState == TelephonyManager.EXTRA_STATE_RINGING){
                    ring()
                }
                //outgoing call
                else if(newState == TelephonyManager.EXTRA_STATE_OFFHOOK){
                    outgoingCall()
                }
            }
            CallState.Ringing -> {
                if(newState == TelephonyManager.EXTRA_STATE_IDLE){
                    missCall()
                }
                else if(newState == TelephonyManager.EXTRA_STATE_OFFHOOK){
                    incomingCall()
                }
            }
            CallState.IncomingCall -> {
                //call ended
                if(newState == TelephonyManager.EXTRA_STATE_IDLE){
                    endIncomingCall()
                }
            }
            CallState.OutgoingCall -> {
                //call ended
                if(newState == TelephonyManager.EXTRA_STATE_IDLE){
                    endOutgoingCall()
                }
            }
        }
    }

    private fun ring(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.Idle) return
        changeState(CallState.Ringing)
        onStateChanged?.invoke()
    }

    private fun missCall(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.Ringing) return
        changeState(CallState.Idle)
        onStateChanged?.invoke()
    }

    private fun incomingCall(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.Ringing) return
        changeState(CallState.IncomingCall)
        onStateChanged?.invoke()
    }

    private fun outgoingCall(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.Idle) return
        changeState(CallState.OutgoingCall)
        onStateChanged?.invoke()
    }

    private fun endIncomingCall(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.IncomingCall) return
        changeState(CallState.Idle)
        onStateChanged?.invoke()
    }

    private fun endOutgoingCall(onStateChanged: (() -> Unit)? = null) {
        if (currentState.state != CallState.OutgoingCall) return
        changeState(CallState.Idle)
        onStateChanged?.invoke()
    }

    companion object {
        var currentState: CallStateHolder = CallStateHolder()
    }

}