package bme.vik.diplomathesis.domain.usecases.auth

import bme.vik.diplomathesis.data.auth.AuthenticationService

class AuthUseCases(
    val repository: AuthenticationService,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val signInAnonymouslyUseCase: SignInAnonymouslyUseCase
)