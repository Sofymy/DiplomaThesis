package bme.vik.diplomathesis.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.vik.diplomathesis.model.data.RunningApplicationsHolder
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _runningApplicationsHolder = mutableStateOf(RunningApplicationsHolder())
    val runningApplicationsHolder: State<RunningApplicationsHolder> = _runningApplicationsHolder


    fun startService() {
        repository.startService()
    }

    fun signInAnonymously(){
        viewModelScope.launch {
            repository.signInAnonimously()
        }
    }

    fun addListener() {
        viewModelScope.launch() {
            repository.addListener()
        }
    }

    fun removeListener() {
        viewModelScope.launch() {
            repository.removeListener()
        }
    }


}