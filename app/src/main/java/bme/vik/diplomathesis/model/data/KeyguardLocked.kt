package bme.vik.diplomathesis.model.data

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KeyguardLocked(
    var keyguardLocked: Boolean = false,
    var timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
): Serializable