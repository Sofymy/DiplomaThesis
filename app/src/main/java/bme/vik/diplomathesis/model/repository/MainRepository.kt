package bme.vik.diplomathesis.model.repository

import bme.vik.diplomathesis.model.data.CallStateHolder
import bme.vik.diplomathesis.model.data.KeyguardLocked
import bme.vik.diplomathesis.model.data.MobileTrafficBytes
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder

interface MainRepository {
    fun startService()
    fun stopService()
    fun removeListener()
    fun addListener()
    suspend fun signInAnonimously()
    fun saveMobileTrafficBytes(
        mobileTrafficBytes: MobileTrafficBytes,
        onResult: (Throwable?) -> Unit
    )
    fun saveRunningApplications(
        runningApplicationsHolder: RunningApplicationsHolder,
        onResult: (Throwable?) -> Unit
    )
    fun saveKeyguardLocked(
        keyguardLocked: KeyguardLocked,
        onResult: (Throwable?) -> Unit
    )
    fun saveCallState(
        callStateHolder: CallStateHolder,
        onResult: (Throwable?) -> Unit
    )
}