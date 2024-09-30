package bme.vik.diplomathesis.domain.model.running_applications

import java.io.Serializable

data class RunningApplicationsHolder(
    private var _runningApplicationInfos: MutableList<RunningApplicationInfo> = mutableListOf()
): Serializable{
    var runningApplications = _runningApplicationInfos
}