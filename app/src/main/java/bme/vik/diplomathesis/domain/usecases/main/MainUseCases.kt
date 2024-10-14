package bme.vik.diplomathesis.domain.usecases.main

import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.usecases.auth.GetCurrentUserUseCase

class MainUseCases(
    val repository: MainRepository,
    val listenToLoggingCollection: ListenToLoggingCollection
)