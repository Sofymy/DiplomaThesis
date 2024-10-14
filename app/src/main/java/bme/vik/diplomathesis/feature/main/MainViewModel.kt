package bme.vik.diplomathesis.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.vik.diplomathesis.domain.model.logging.Logging
import bme.vik.diplomathesis.domain.usecases.auth.AuthUseCases
import bme.vik.diplomathesis.domain.usecases.main.MainUseCases
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val currentUser: FirebaseUser? = null,
    val logging: List<Logging> = emptyList()
)

sealed class MainUserEvent {
    data object CheckCurrentUser: MainUserEvent()
    data object SignInAnonymously: MainUserEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val mainUseCases: MainUseCases
): ViewModel() {

    init {
        listenToLoggingCollection()
    }

    private val _state = MutableStateFlow(MainUiState())
    val state = _state

    fun onEvent(event: MainUserEvent) {
        when(event) {
            MainUserEvent.CheckCurrentUser -> {
                checkCurrentUser()
            }

            MainUserEvent.SignInAnonymously -> {
                signInAnonymously()
            }

        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authUseCases.getCurrentUserUseCase()
            _state.update { it.copy(currentUser = user, isLoading = false) }
        }
    }

    private fun signInAnonymously(){
        viewModelScope.launch {
            authUseCases.signInAnonymouslyUseCase()
        }
    }

    private fun listenToLoggingCollection() {
        viewModelScope.launch {
            mainUseCases.listenToLoggingCollection().collect { loggingList ->
                _state.update {
                    it.copy(logging = loggingList)
                }
            }
        }
    }
}