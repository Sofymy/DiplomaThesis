package bme.vik.diplomathesis.data.repository

import bme.vik.diplomathesis.domain.model.CellInfo
import bme.vik.diplomathesis.domain.model.KeyguardLockedInfo
import bme.vik.diplomathesis.domain.model.MemoryUsageInfo
import bme.vik.diplomathesis.domain.model.MobileTrafficInfo
import bme.vik.diplomathesis.domain.model.NetworkInfo
import bme.vik.diplomathesis.domain.model.PowerConnectionInfo
import bme.vik.diplomathesis.domain.model.running_applications.RunningApplicationsHolder
import bme.vik.diplomathesis.domain.model.StorageUsageInfo
import bme.vik.diplomathesis.domain.model.call_state.CallStateHolder
import bme.vik.diplomathesis.domain.model.logging.DeviceMetric
import bme.vik.diplomathesis.domain.model.logging.Logging
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun startServices(loggingMetrics: List<DeviceMetric>)
    fun stopServices()

    suspend fun signInAnonymously()

    suspend fun saveMobileTrafficBytes(
        mobileTrafficInfo: MobileTrafficInfo,
        onResult: (Throwable?) -> Unit
    )
    suspend fun saveRunningApplications(
        runningApplicationsHolder: RunningApplicationsHolder,
        onResult: (Throwable?) -> Unit
    )
    suspend fun saveKeyguardLocked(
        keyguardLockedInfo: KeyguardLockedInfo,
        onResult: (Throwable?) -> Unit
    )
    suspend fun saveCallState(
        callStateHolder: CallStateHolder,
        onResult: (Throwable?) -> Unit
    )
    suspend fun savePowerConnection(
        powerConnectionInfo: PowerConnectionInfo,
        onResult: (Throwable?) -> Unit
    )

    suspend fun saveNetwork(
        networkInfo: NetworkInfo,
        onResult: (Throwable?) -> Unit
    )

    suspend fun saveCell(
        cellInfo: CellInfo,
        onResult: (Throwable?) -> Unit
    )

    suspend fun saveMemoryUsage(
        memoryUsageInfo: MemoryUsageInfo,
        onResult: (Throwable?) -> Unit
    )

    suspend fun saveStorageUsage(
        storageUsageInfo: StorageUsageInfo,
        onResult: (Throwable?) -> Unit
    )

    fun listenToLoggingCollection(): Flow<List<Logging>>
}