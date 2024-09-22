package bme.vik.diplomathesis.model.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PowerConnection(
    val batteryPct: Int?,
    val isCharging: Boolean?,
)
