package bme.vik.diplomathesis.data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import bme.vik.diplomathesis.domain.model.*
import bme.vik.diplomathesis.domain.model.call_state.CallStateHolder
import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.domain.model.running_applications.RunningApplicationsHolder
import bme.vik.diplomathesis.domain.service.KeyguardLockedService
import bme.vik.diplomathesis.domain.service.LocationService
import bme.vik.diplomathesis.domain.service.MemoryUsageService
import bme.vik.diplomathesis.domain.service.MobileTrafficBytesService
import bme.vik.diplomathesis.domain.service.NetworkService
import bme.vik.diplomathesis.domain.service.RunningApplicationsService
import bme.vik.diplomathesis.domain.service.StorageUsageService
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val firebaseFirestore: FirebaseFirestore,
    private val authenticationService: AuthenticationService
) : MainRepository {

    private fun getDate(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())

    override suspend fun signInAnonymously() {
        authenticationService.signInAnonymously()
    }

    override fun startServices() {
        val services = listOf(
            RunningApplicationsService::class.java,
            MobileTrafficBytesService::class.java,
            KeyguardLockedService::class.java,
            LocationService::class.java,
            NetworkService::class.java,
            MemoryUsageService::class.java,
            StorageUsageService::class.java
        )
        services.forEach {
            startService(it)
        }
    }

    private fun startService(serviceClass: Class<*>) {
        val intent = Intent(applicationContext, serviceClass)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }
    }

    override fun stopServices() {
        val services = listOf(
            RunningApplicationsService::class.java,
            MobileTrafficBytesService::class.java,
            KeyguardLockedService::class.java,
            LocationService::class.java,
            NetworkService::class.java,
            MemoryUsageService::class.java,
            StorageUsageService::class.java
        )
        services.forEach { stopService(it) }
    }

    private fun stopService(serviceClass: Class<*>) {
        applicationContext.stopService(Intent(applicationContext, serviceClass))
    }

    override suspend fun saveRunningApplications(
        runningApplicationsHolder: RunningApplicationsHolder,
        onResult: (Throwable?) -> Unit
    ) {
        saveDataToFirestore(RUNNING_APPLICATIONS_COLLECTION, runningApplicationsHolder, onResult)
    }

    override suspend fun saveMobileTrafficBytes(
        mobileTrafficInfo: MobileTrafficInfo,
        onResult: (Throwable?) -> Unit
    ) {
        saveDataToFirestore(MOBILE_TRAFFIC_BYTES_COLLECTION, mobileTrafficInfo, onResult)
    }

    override suspend fun saveKeyguardLocked(
        keyguardLockedInfo: KeyguardLockedInfo,
        onResult: (Throwable?) -> Unit
    ) {
        saveDataToFirestore(KEYGUARD_LOCKED_COLLECTION, keyguardLockedInfo, onResult)
    }

    override suspend fun saveNetwork(networkInfo: NetworkInfo, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(NETWORK_COLLECTION, networkInfo, onResult)
    }

    override suspend fun saveCallState(callStateHolder: CallStateHolder, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(CALL_STATE_COLLECTION, callStateHolder, onResult)
    }

    override suspend fun saveCell(cellInfo: CellInfo, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(CELL_COLLECTION, cellInfo, onResult)
    }

    override suspend fun savePowerConnection(powerConnectionInfo: PowerConnectionInfo, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(POWER_CONNECTION_COLLECTION, powerConnectionInfo, onResult)
    }

    override suspend fun saveMemoryUsage(memoryUsageInfo: MemoryUsageInfo, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(MEMORY_USAGE_COLLECTION, memoryUsageInfo, onResult)
    }

    override suspend fun saveStorageUsage(storageUsageInfo: StorageUsageInfo, onResult: (Throwable?) -> Unit) {
        saveDataToFirestore(STORAGE_USAGE_COLLECTION, storageUsageInfo, onResult)
    }

    private suspend fun <T : Any> saveDataToFirestore(collection: String, data: T, onResult: (Throwable?) -> Unit) {
        val userId = authenticationService.getCurrentUser()?.uid
        if (userId != null) {
            firebaseFirestore.collection(collection)
                .document(userId)
                .collection("timestamps")
                .document(getDate())
                .set(data)
                .addOnCompleteListener { onResult(it.exception) }
                .addOnFailureListener { onResult(it) }
        }
    }

    companion object {
        private const val RUNNING_APPLICATIONS_COLLECTION = "runningapplications"
        private const val MOBILE_TRAFFIC_BYTES_COLLECTION = "mobiletrafficbytes"
        private const val KEYGUARD_LOCKED_COLLECTION = "keyguardlocked"
        private const val CALL_STATE_COLLECTION = "callstate"
        private const val NETWORK_COLLECTION = "networks"
        private const val CELL_COLLECTION = "cells"
        private const val POWER_CONNECTION_COLLECTION = "powerconnections"
        private const val MEMORY_USAGE_COLLECTION = "memoryusages"
        private const val STORAGE_USAGE_COLLECTION = "storageusages"
    }
}
