package bme.vik.diplomathesis.domain.usecases.auth

import bme.vik.diplomathesis.data.auth.AuthenticationService
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val authenticationService: AuthenticationService
) {
    suspend operator fun invoke() = authenticationService.signInAnonymously()
}