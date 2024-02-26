package bme.vik.diplomathesis.model.impl

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import bme.vik.diplomathesis.model.data.DeviceInfo
import bme.vik.diplomathesis.model.data.Response
import bme.vik.diplomathesis.model.data.User
import bme.vik.diplomathesis.model.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
): AuthenticationRepository {

    override val currentUserId: String
        get() = firebaseAuth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) } ?: User())
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }

    override suspend fun signInAnonymously(firebaseFirestore: FirebaseFirestore) {
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
                .set(getDeviceInfo())
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceInfo() : Response<DeviceInfo> {

        val response = try {
            val deviceModel = Build.MODEL
            val deviceBrand = Build.MANUFACTURER
            val deviceName = Build.DEVICE

            val info = DeviceInfo(
                deviceModel = deviceModel,
                deviceBrand = deviceBrand,
                deviceName = deviceName
            )

            Response.Success(info)

        } catch (e: Exception) {
            Response.Error(e.message ?: "Unexpected error occurred")
        }

        return response

    }


}