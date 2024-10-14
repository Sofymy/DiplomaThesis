package bme.vik.diplomathesis.domain.di

import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.usecases.main.ListenToLoggingCollection
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
        listenToLoggingCollection: ListenToLoggingCollection
    ): MainUseCases {
        return MainUseCases(
            repository = repository,
            listenToLoggingCollection = listenToLoggingCollection
        )
    }

    @Provides
    @Singleton
    fun provideListenToLoggingCollectionUseCase(
        repository: MainRepository
    ): ListenToLoggingCollection {
        return ListenToLoggingCollection(repository)
    }


}