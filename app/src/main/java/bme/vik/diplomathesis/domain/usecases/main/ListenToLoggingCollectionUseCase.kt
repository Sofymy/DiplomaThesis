package bme.vik.diplomathesis.domain.usecases.main

import bme.vik.diplomathesis.data.repository.MainRepository
import javax.inject.Inject

class ListenToLoggingCollectionUseCase @Inject constructor(
    private val repository: MainRepository
) {
    operator fun invoke() = repository.listenToLoggingCollection()
}