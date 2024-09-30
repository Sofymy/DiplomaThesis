package bme.vik.diplomathesis.data.auth

import bme.vik.diplomathesis.domain.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface AuthenticationService {

    suspend fun signInAnonymously()
    suspend fun createNewDocumentForNewUser(firebaseFirestore: FirebaseFirestore)
    suspend fun getCurrentUser(): FirebaseUser?
}