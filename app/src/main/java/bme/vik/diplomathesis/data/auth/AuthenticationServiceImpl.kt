package bme.vik.diplomathesis.data.auth

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import bme.vik.diplomathesis.domain.model.DeviceInfo
import bme.vik.diplomathesis.domain.utils.Response
import bme.vik.diplomathesis.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthenticationServiceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context,
): AuthenticationService {

    override suspend fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser


    override suspend fun signInAnonymously() {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInAnonymously:success")
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.exception)
            }
        }.await()
        createNewDocumentForNewUser(firebaseFirestore)

    }

    override suspend fun createNewDocumentForNewUser(firebaseFirestore: FirebaseFirestore){
        firebaseAuth.currentUser?.let {
            firebaseFirestore
                .collection("devices")
                .document(it.uid)
                .set(saveDeviceInfo())
        }
    }

    @SuppressLint("HardwareIds")
    private fun saveDeviceInfo() : Response<DeviceInfo> {

        val response = try {
            val deviceModel = Build.MODEL
            val deviceBrand = Build.MANUFACTURER
            val deviceName = Build.DEVICE

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val deviceTac = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager!!.typeAllocationCode
            } else {
                null
            }

            val info = DeviceInfo(
                deviceModel = deviceModel,
                deviceBrand = deviceBrand,
                deviceName = deviceName,
                deviceTac = deviceTac
            )

            Response.Success(info)

        } catch (e: Exception) {
            Response.Error(e.message ?: "Unexpected error occurred")
        }

        return response

    }


}