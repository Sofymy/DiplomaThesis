package bme.vik.diplomathesis.model.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CallStateHolder(
    var state: CallState = CallState.Idle,
    var timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
)
