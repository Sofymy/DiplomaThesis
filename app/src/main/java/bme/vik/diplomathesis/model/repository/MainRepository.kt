package bme.vik.diplomathesis.model.repository

import bme.vik.diplomathesis.model.data.Cell
import bme.vik.diplomathesis.model.data.KeyguardLocked
import bme.vik.diplomathesis.model.data.MemoryUsage
import bme.vik.diplomathesis.model.data.MobileTrafficBytes
import bme.vik.diplomathesis.model.data.Network
import bme.vik.diplomathesis.model.data.PowerConnection
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder
import bme.vik.diplomathesis.model.data.StorageUsage
import bme.vik.diplomathesis.model.data.callstate.CallStateHolder
import kotlinx.coroutines.flow.Flow

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
    fun savePowerConnection(
        powerConnection: PowerConnection,
        onResult: (Throwable?) -> Unit
    )

    fun saveNetwork(
        network: Network,
        onResult: (Throwable?) -> Unit
    )

    fun saveCell(
        cell: Cell,
        onResult: (Throwable?) -> Unit
    )

    fun saveMemoryUsage(
        memoryUsage: MemoryUsage,
        onResult: (Throwable?) -> Unit
    )

    fun saveStorageUsage(
        storageUsage: StorageUsage,
        onResult: (Throwable?) -> Unit
    )
}