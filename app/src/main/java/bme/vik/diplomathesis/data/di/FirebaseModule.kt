package bme.vik.diplomathesis.data.di

import android.content.Context
import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.data.auth.AuthenticationServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Singleton
    @Provides
    fun provideAuthenticationService(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): AuthenticationService {
        return AuthenticationServiceImpl(
            firebaseAuth = firebaseAuth,
            firebaseFirestore = firestore,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()
}