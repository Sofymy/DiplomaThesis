package bme.vik.diplomathesis.domain.usecases.main

import bme.vik.diplomathesis.data.repository.MainRepository

class MainUseCases(
    val repository: MainRepository,
    val listenToLoggingCollectionUseCase: ListenToLoggingCollectionUseCase
)