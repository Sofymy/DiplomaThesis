package bme.vik.diplomathesis.model.data

import java.io.Serializable

data class RunningApplicationsHolder(
    private var _runningApplications: MutableList<RunningApplication> = mutableListOf()
): Serializable{
    var runningApplications = _runningApplications
}