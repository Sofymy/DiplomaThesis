package bme.vik.diplomathesis.di

import android.content.Context
import bme.vik.diplomathesis.model.impl.AuthenticationRepositoryImpl
import bme.vik.diplomathesis.model.impl.MainRepositoryImpl
import bme.vik.diplomathesis.model.repository.AuthenticationRepository
import bme.vik.diplomathesis.model.repository.MainRepository
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
object HiltModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        @ApplicationContext applicationContext: Context,
        firebaseFirestore: FirebaseFirestore,
        authenticationRepository: AuthenticationRepository
    ): MainRepository {
        return MainRepositoryImpl(
            applicationContext,
            firebaseFirestore,
            authenticationRepository
        )
    }

    @Singleton
    @Provides
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
        @ApplicationContext context: Context
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(
            firebaseAuth,
            context
        )
    }

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()
}
