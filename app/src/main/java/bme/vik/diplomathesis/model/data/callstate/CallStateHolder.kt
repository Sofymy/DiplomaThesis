package bme.vik.diplomathesis.model.data.callstate

import bme.vik.diplomathesis.model.data.callstate.CallState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CallStateHolder(
    var state: CallState = CallState.Idle,
    var timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
)
