package bme.vik.diplomathesis.domain.di

import bme.vik.diplomathesis.data.auth.AuthenticationService
import bme.vik.diplomathesis.domain.usecases.auth.AuthUseCases
import bme.vik.diplomathesis.domain.usecases.auth.GetCurrentUserUseCase
import bme.vik.diplomathesis.domain.usecases.auth.SignInAnonymouslyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCasesModule {

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authenticationService: AuthenticationService,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        signInAnonymouslyUseCase: SignInAnonymouslyUseCase
    ): AuthUseCases {
        return AuthUseCases(
            repository = authenticationService,
            getCurrentUserUseCase = getCurrentUserUseCase,
            signInAnonymouslyUseCase = signInAnonymouslyUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        authenticationService: AuthenticationService
    ): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authenticationService)
    }

    @Provides
    @Singleton
    fun provideSignInAnonymouslyUseCase(
        authenticationService: AuthenticationService
    ): SignInAnonymouslyUseCase {
        return SignInAnonymouslyUseCase(authenticationService)
    }

}