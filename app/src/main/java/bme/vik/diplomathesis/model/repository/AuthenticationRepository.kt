package bme.vik.diplomathesis.model.repository

import bme.vik.diplomathesis.model.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun signInAnonymously(firebaseFirestore: FirebaseFirestore)
    val currentUser: Flow<User>
    val currentUserId: String
    suspend fun createNewDocumentForNewUser(firebaseFirestore: FirebaseFirestore)
}