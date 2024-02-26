package bme.vik.diplomathesis.model.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import bme.vik.diplomathesis.model.data.CallStateHolder
import bme.vik.diplomathesis.model.data.KeyguardLocked
import bme.vik.diplomathesis.model.data.MobileTrafficBytes
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder
import bme.vik.diplomathesis.model.repository.AuthenticationRepository
import bme.vik.diplomathesis.model.repository.MainRepository
import bme.vik.diplomathesis.model.service.KeyguardLockedService
import bme.vik.diplomathesis.model.service.MobileTrafficBytesService
import bme.vik.diplomathesis.model.service.RunningApplicationsService
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val firebaseFirestore: FirebaseFirestore,
    private val authenticationRepository: AuthenticationRepository
): MainRepository {

    //val runningApplications = MutableLiveData<RunningApplicationsHolder>()
    //val mobileTrafficBytes = MutableLiveData<MobileTrafficBytes>()
    //val keyguardLocked = MutableLiveData<KeyguardLocked>()

    override suspend fun signInAnonimously() {
        authenticationRepository.signInAnonymously(firebaseFirestore)
    }

    override fun startService() {
        val startIntentRunningApplications = Intent(applicationContext, RunningApplicationsService::class.java)
        val startIntentMobileTrafficBytes = Intent(applicationContext, MobileTrafficBytesService::class.java)
        val startKeyguardLockedService = Intent(applicationContext, KeyguardLockedService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(startIntentRunningApplications)
            applicationContext.startForegroundService(startIntentMobileTrafficBytes)
            applicationContext.startForegroundService(startKeyguardLockedService)
        }
        else {
            applicationContext.startService(startIntentRunningApplications)
            applicationContext.startService(startIntentMobileTrafficBytes)
            applicationContext.startService(startKeyguardLockedService)
        }
    }

    override fun stopService() {
        val stopIntentRunningApplications = Intent(applicationContext, RunningApplicationsService::class.java)
        val stopIntentMobileTrafficBytes = Intent(applicationContext, MobileTrafficBytesService::class.java)
        val stopKeyguardLockedService = Intent(applicationContext, KeyguardLockedService::class.java)

        applicationContext.stopService(stopIntentRunningApplications)
        applicationContext.stopService(stopIntentMobileTrafficBytes)
        applicationContext.stopService(stopKeyguardLockedService)
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
                onResult(it.exception)
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

    override fun addListener() {
    }

    override fun removeListener() {
    }


    companion object {
        private const val RUNNING_APPLICATIONS_COLLECTION = "runningapplications"
        private const val MOBILE_TRAFFIC_BYTES_COLLECTION = "mobiletrafficbytes"
        private const val KEYGUARD_LOCKED_COLLECTION = "keyguardlocked"
        private const val CALL_STATE_COLLECTION = "callstate"
    }

}
