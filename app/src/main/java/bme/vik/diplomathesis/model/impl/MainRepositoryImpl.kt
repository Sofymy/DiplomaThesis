package bme.vik.diplomathesis.model.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import bme.vik.diplomathesis.model.data.Cell
import bme.vik.diplomathesis.model.data.KeyguardLocked
import bme.vik.diplomathesis.model.data.MemoryUsage
import bme.vik.diplomathesis.model.data.MobileTrafficBytes
import bme.vik.diplomathesis.model.data.Network
import bme.vik.diplomathesis.model.data.PowerConnection
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder
import bme.vik.diplomathesis.model.data.StorageUsage
import bme.vik.diplomathesis.model.data.callstate.CallStateHolder
import bme.vik.diplomathesis.model.repository.AuthenticationRepository
import bme.vik.diplomathesis.model.repository.MainRepository
import bme.vik.diplomathesis.model.service.KeyguardLockedService
import bme.vik.diplomathesis.model.service.LocationService
import bme.vik.diplomathesis.model.service.MemoryUsageService
import bme.vik.diplomathesis.model.service.MobileTrafficBytesService
import bme.vik.diplomathesis.model.service.NetworkService
import bme.vik.diplomathesis.model.service.RunningApplicationsService
import bme.vik.diplomathesis.model.service.StorageUsageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val firebaseFirestore: FirebaseFirestore,
    private val authenticationRepository: AuthenticationRepository
): MainRepository {


    override suspend fun signInAnonimously() {
        authenticationRepository.signInAnonymously(firebaseFirestore)
    }

    override fun startService(
    ) {
        val startIntentRunningApplications = Intent(applicationContext, RunningApplicationsService::class.java)
        val startIntentMobileTrafficBytes = Intent(applicationContext, MobileTrafficBytesService::class.java)
        val startKeyguardLockedService = Intent(applicationContext, KeyguardLockedService::class.java)
        val startLocationService = Intent(applicationContext, LocationService::class.java)
        val startCellService = Intent(applicationContext, NetworkService::class.java)
        val startMemoryUsageService = Intent(applicationContext, MemoryUsageService::class.java)
        val startStorageUsageService = Intent(applicationContext, StorageUsageService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(startIntentRunningApplications)
            applicationContext.startForegroundService(startIntentMobileTrafficBytes)
            applicationContext.startForegroundService(startKeyguardLockedService)
            applicationContext.startForegroundService(startLocationService)
            applicationContext.startForegroundService(startCellService)
            applicationContext.startForegroundService(startMemoryUsageService)
            applicationContext.startForegroundService(startStorageUsageService)
        }
        else {
            applicationContext.startService(startIntentRunningApplications)
            applicationContext.startService(startIntentMobileTrafficBytes)
            applicationContext.startService(startKeyguardLockedService)
            applicationContext.startService(startLocationService)
            applicationContext.startService(startCellService)
            applicationContext.startService(startMemoryUsageService)
            applicationContext.startService(startStorageUsageService)
        }
    }

    override fun stopService() {
        val stopIntentRunningApplications = Intent(applicationContext, RunningApplicationsService::class.java)
        val stopIntentMobileTrafficBytes = Intent(applicationContext, MobileTrafficBytesService::class.java)
        val stopKeyguardLockedService = Intent(applicationContext, KeyguardLockedService::class.java)
        val stopLocationService = Intent(applicationContext, LocationService::class.java)
        val stopCellService = Intent(applicationContext, NetworkService::class.java)
        val stopMemoryUsageService = Intent(applicationContext, MemoryUsageService::class.java)
        val stopStorageUsageService = Intent(applicationContext, StorageUsageService::class.java)


        applicationContext.stopService(stopIntentRunningApplications)
        applicationContext.stopService(stopIntentMobileTrafficBytes)
        applicationContext.stopService(stopKeyguardLockedService)
        applicationContext.stopService(stopLocationService)
        applicationContext.stopService(stopCellService)
        applicationContext.stopService(stopMemoryUsageService)
        applicationContext.stopService(stopStorageUsageService)
    }

    override fun getRunningApplications(
        onResult: (Throwable?) -> Unit
    ): Flow<RunningApplicationsHolder> = callbackFlow {
        val document = firebaseFirestore
            .collection(RUNNING_APPLICATIONS_COLLECTION)
            .document(authenticationRepository.currentUserId)
        val listener = document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val runningApplicationsHolderObject = snapshot.toObject(RunningApplicationsHolder::class.java)
                if (runningApplicationsHolderObject != null) {
                }
            }
        }
        awaitClose {
            listener.remove()
            close()
        }
    }

    override fun saveRunningApplications(
        runningApplicationsHolder: RunningApplicationsHolder,
        onResult: (Throwable?) -> Unit
    ){

        firebaseFirestore
            .collection(RUNNING_APPLICATIONS_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(runningApplicationsHolder)
            .addOnCompleteListener {
            }
            .addOnFailureListener {
            }
    }

    override fun saveMobileTrafficBytes(
        mobileTrafficBytes: MobileTrafficBytes,
        onResult: (Throwable?) -> Unit
    ){
        firebaseFirestore
            .collection(MOBILE_TRAFFIC_BYTES_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(mobileTrafficBytes)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun saveKeyguardLocked(
        keyguardLocked: KeyguardLocked,
        onResult: (Throwable?) -> Unit
    ) {
        firebaseFirestore
            .collection(KEYGUARD_LOCKED_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(keyguardLocked)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun saveNetwork(
        network: Network,
        onResult: (Throwable?) -> Unit
    ) {
        firebaseFirestore
            .collection(NETWORK_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(network)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }


    override fun saveCallState(
        callStateHolder: CallStateHolder,
        onResult: (Throwable?) -> Unit) {

        firebaseFirestore
            .collection(CALL_STATE_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(callStateHolder)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun saveCell(
        cell: Cell,
        onResult: (Throwable?) -> Unit) {

        firebaseFirestore
            .collection(CELL_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(cell)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun savePowerConnection(
        powerConnection: PowerConnection,
        onResult: (Throwable?) -> Unit,
    ) {
        firebaseFirestore
            .collection(POWER_CONNECTION_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(powerConnection)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun saveMemoryUsage(
        memoryUsage: MemoryUsage,
        onResult: (Throwable?) -> Unit,
    ) {
        firebaseFirestore
            .collection(MEMORY_USAGE_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(memoryUsage)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun saveStorageUsage(
        storageUsage: StorageUsage,
        onResult: (Throwable?) -> Unit,
    ) {
        firebaseFirestore
            .collection(STORAGE_USAGE_COLLECTION)
            .document(authenticationRepository.currentUserId)
            .set(storageUsage)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun addListener() {
    }

    override fun removeListener() {
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

