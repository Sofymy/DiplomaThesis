package bme.vik.diplomathesis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.vik.diplomathesis.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {
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