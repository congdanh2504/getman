package com.example.getman.ui.login

import com.example.getman.base.ViewModel
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val localRepository: LocalRepository
): ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Init)
    val loginState = _loginState.asStateFlow()

    private val _saveUserState = MutableStateFlow(false)
    val saveUserState = _saveUserState.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        val user = userRepository.login(email, password)
        if (user != null) {
            _loginState.emit(LoginUiState.Success(user))
        } else {
            _loginState.emit(LoginUiState.Error("Invalid email or password"))
        }
    }

    fun saveUserToLocal(user: User) = viewModelScope.launch {
        localRepository.saveUser(user)
        _saveUserState.emit(true)
    }

}

sealed interface LoginUiState {
    object Init: LoginUiState
    class Error(val message: String): LoginUiState
    class Success(val user: User): LoginUiState
}