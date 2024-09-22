package bme.vik.diplomathesis.model.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MobileTrafficBytes(
    val mobileTxBytes: Long = 0,
    val mobileRxBytes: Long = 0,
)