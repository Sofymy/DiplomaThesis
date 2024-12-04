package bme.vik.diplomathesis.data.repository

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import bme.vik.diplomathesis.domain.model.*
import bme.vik.diplomathesis.domain.model.call_state.CallStateHolder
import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.domain.model.logging.DeviceMetric
import bme.vik.diplomathesis.domain.model.logging.Logging
import bme.vik.diplomathesis.domain.model.running_applications.RunningApplicationsHolder
import bme.vik.diplomathesis.domain.receiver.CallStateReceiver
import bme.vik.diplomathesis.domain.receiver.PowerConnectionReceiver
import bme.vik.diplomathesis.domain.service.CellService
import bme.vik.diplomathesis.domain.service.KeyguardLockedService
import bme.vik.diplomathesis.domain.service.MemoryUsageService
import bme.vik.diplomathesis.domain.service.MobileTrafficBytesService
import bme.vik.diplomathesis.domain.service.NetworkService
import bme.vik.diplomathesis.domain.service.RunningApplicationsService
import bme.vik.diplomathesis.domain.service.StorageUsageService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val firebaseFirestore: FirebaseFirestore,
    private val authenticationService: AuthenticationService
) : MainRepository {

    private val metricToServiceMap = mapOf(
        DeviceMetric.MEMORY_USAGE to MemoryUsageService::class.java,
        DeviceMetric.MOBILE_DATA_TRAFFIC to MobileTrafficBytesService::class.java,
        DeviceMetric.LOCK_SCREEN_DATA to KeyguardLockedService::class.java,
        DeviceMetric.STORAGE_INFORMATION to StorageUsageService::class.java,
        DeviceMetric.MOBILE_NETWORK_DATA to NetworkService::class.java,
        DeviceMetric.DEVICE_INFORMATION to RunningApplicationsService::class.java,
        DeviceMetric.CELL_INFORMATION to CellService::class.java
    )

    private val metricToBroadcastReceiverMap = mapOf(
        DeviceMetric.BATTERY_DATA to PowerConnectionReceiver::class.java,
        DeviceMetric.CALLS to CallStateReceiver::class.java
    )

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerReceiverForMetric(metric: DeviceMetric) {
        val receiverClass = metricToBroadcastReceiverMap[metric]

        receiverClass?.let {
            val receiver = it.newInstance() as BroadcastReceiver
            val intentFilter = when (metric) {
                DeviceMetric.BATTERY_DATA -> IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                DeviceMetric.CALLS -> IntentFilter("android.intent.action.PHONE_STATE")
                else -> IntentFilter()
            }

            applicationContext.registerReceiver(receiver, intentFilter)
        }
    }

    private fun getDate(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())

    override suspend fun signInAnonymously() {
        authenticationService.signInAnonymously()
    }

    private fun groupMetricsByTac(loggingList: List<Logging>): Map<String, List<DeviceMetric>> {
        return loggingList
            .flatMap { logging ->
                logging.deviceTac.map { tac -> tac to logging.loggingMetrics }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, metricsList) -> metricsList.flatten().distinct() }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun listenToLoggingCollection(): Flow<List<Logging>> = callbackFlow {
        val collectionRef = firebaseFirestore.collection("logging")
        val listenerRegistration = collectionRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                close(it)
                return@addSnapshotListener
            }

            val loggingList = snapshot?.documents?.mapNotNull { it.toObject(Logging::class.java) } ?: emptyList()
            val groupedLogging = groupMetricsByTac(loggingList)

            val deviceTac = (applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
                ?.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q }
                ?.typeAllocationCode ?: ""

            val currentMetrics = groupedLogging[deviceTac] ?: emptyList()

            startServices(currentMetrics)
            startBroadcastRecivers(currentMetrics)

            stopServices(currentMetrics)
            stopBroadcastReceivers(currentMetrics)

            trySend(loggingList)
        }

        awaitClose { listenerRegistration.remove() }
    }

    override fun stopServices(loggingMetrics: List<DeviceMetric>) {
        val metrics = metricToServiceMap.keys
        val metricsToStop = metrics.filterNot { metric ->
            loggingMetrics.contains(metric)
        }

        metricsToStop.forEach { metric ->
            stopService(metricToServiceMap[metric]!!)
        }
    }


    override fun startServices(loggingMetrics: List<DeviceMetric>) {

        loggingMetrics.forEach { metric ->
            metricToServiceMap[metric]?.let { serviceClass ->
                startService(serviceClass)
            }
        }
    }

    override fun startBroadcastRecivers(loggingMetrics: List<DeviceMetric>) {
        loggingMetrics.forEach { metric ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiverForMetric(metric)
            }
        }
    }

    private fun stopBroadcastReceivers(currentMetrics: List<DeviceMetric>) {
        val activeMetrics = metricToBroadcastReceiverMap.keys
        val metricsToStop = activeMetrics.filterNot { metric ->
            currentMetrics.contains(metric)
        }

        metricsToStop.forEach { metric ->
            val receiverClass = metricToBroadcastReceiverMap[metric]
            val receiver = receiverClass?.newInstance() as BroadcastReceiver
            stopBroadcastReceiver(receiver)
        }
    }

    private fun stopBroadcastReceiver(broadcastReceiver: BroadcastReceiver) {
        try {
            applicationContext.unregisterReceiver(broadcastReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
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


    private fun stopService(serviceClass: Class<*>) {
        Log.d("ServiceStop", "Attempting to stop service: ${serviceClass.simpleName}")
        val intent = Intent(applicationContext, serviceClass)
        try {
            val stopped = applicationContext.stopService(intent)
            Log.d("ServiceStop", "Service stopped: $stopped")
        } catch (e: Exception) {
            Log.e("ServiceStopError", "Error stopping service: ${e.message}", e)
        }
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
