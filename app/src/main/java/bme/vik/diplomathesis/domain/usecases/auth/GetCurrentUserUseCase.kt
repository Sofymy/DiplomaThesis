package bme.vik.diplomathesis.domain.usecases.auth

import bme.vik.diplomathesis.data.auth.AuthenticationService
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authenticationService: AuthenticationService
) {
    suspend operator fun invoke() = authenticationService.getCurrentUser()
}