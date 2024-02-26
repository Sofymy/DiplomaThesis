package bme.vik.diplomathesis.model.data

import android.util.Log
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RunningApplicationsHolder(
    private var _runningApplications: MutableList<RunningApplication> = mutableListOf()
): Serializable{
    var runningApplications = _runningApplications
}