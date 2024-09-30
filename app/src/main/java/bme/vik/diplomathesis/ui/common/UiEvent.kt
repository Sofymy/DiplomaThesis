package bme.vik.diplomathesis.ui.common

sealed class UiEvent {
    data object Success : UiEvent()
    data class Error(val message: String) : UiEvent()
}