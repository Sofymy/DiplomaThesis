package bme.vik.diplomathesis.domain.di

import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.usecases.main.ListenToLoggingCollectionUseCase
import bme.vik.diplomathesis.domain.usecases.main.MainUseCases
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
        listenToLoggingCollectionUseCase: ListenToLoggingCollectionUseCase
    ): MainUseCases {
        return MainUseCases(
            repository = repository,
            listenToLoggingCollectionUseCase = listenToLoggingCollectionUseCase
        )
    }

    @Provides
    @Singleton
    fun provideListenToLoggingCollectionUseCase(
        repository: MainRepository
    ): ListenToLoggingCollectionUseCase {
        return ListenToLoggingCollectionUseCase(repository)
    }


}