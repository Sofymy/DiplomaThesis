package bme.vik.diplomathesis.domain.di

import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.usecases.auth.AuthUseCases
import bme.vik.diplomathesis.domain.usecases.auth.GetCurrentUserUseCase
import bme.vik.diplomathesis.domain.usecases.main.MainUseCases
import bme.vik.diplomathesis.domain.usecases.main.StartServicesUseCase
import bme.vik.diplomathesis.domain.usecases.main.StopServicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainUseCasesModule {

    @Provides
    @Singleton
    fun provideMainUseCases(
        repository: MainRepository,
        startServicesUseCase: StartServicesUseCase,
        stopServicesUseCase: StopServicesUseCase
    ): MainUseCases {
        return MainUseCases(
            repository = repository,
            startServicesUseCase = startServicesUseCase,
            stopServicesUseCase = stopServicesUseCase
        )
    }

    @Provides
    @Singleton
    fun provideStartServicesUseCase(
        repository: MainRepository
    ): StartServicesUseCase {
        return StartServicesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideStopServicesUseCase(
        repository: MainRepository
    ): StopServicesUseCase {
        return StopServicesUseCase(repository)
    }

}