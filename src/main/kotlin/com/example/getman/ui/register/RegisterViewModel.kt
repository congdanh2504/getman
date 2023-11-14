package com.example.getman.ui.register

import com.example.getman.base.ViewModel
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Init)
    val registerState = _registerState.asStateFlow()

    fun register(user: User) = viewModelScope.launch {
        val userWithEmail = userRepository.getUserByEmail(user.email)
        if (userWithEmail != null) {
            _registerState.emit(RegisterUiState.Error("Email is used by another user"))
        } else {
            if (userRepository.saveUser(user)) {
                _registerState.emit(RegisterUiState.Success)
            } else {
                _registerState.emit(RegisterUiState.Error("Error when register"))
            }
        }
    }
}

sealed interface RegisterUiState {
    object Init: RegisterUiState
    object Success: RegisterUiState
    class Error(val message: String): RegisterUiState
}