package bme.vik.diplomathesis.model.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RunningApplication(
    var applicationName: String = "",
    var openTimestamp: String? = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()) + " +0000",
    var closeTimestamp: String? = null
){

}
