package bme.vik.diplomathesis.data.di

import android.content.Context
import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.data.repository.MainRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        @ApplicationContext applicationContext: Context,
        firebaseFirestore: FirebaseFirestore,
        authenticationService: AuthenticationService
    ): MainRepository {
        return MainRepositoryImpl(
            applicationContext,
            firebaseFirestore,
            authenticationService
        )
    }


}