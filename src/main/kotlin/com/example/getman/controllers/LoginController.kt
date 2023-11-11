package com.example.getman.controllers

import com.example.getman.GetManApplication
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.domain.repository.UserRepository
import com.example.getman.utils.applicationScope
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class LoginController: KoinComponent {

    lateinit var registerButton: Button
    lateinit var loginButton: Button
    lateinit var passwordField: PasswordField
    lateinit var emailField: TextField

    private val userRepository: UserRepository by inject()
    private val localRepository: LocalRepository by inject()

    fun handleLogin() {
        applicationScope.launch {
            val user = userRepository.login(emailField.text, passwordField.text)
            if (user != null) {
                localRepository.saveUser(user)
                GetManApplication.instance.navigateToHome()
            } else {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "Error"
                    contentText = "Invalid email or password"
                }.also {
                    it.show()
                }
            }
        }
    }

    fun handleRegister() {
        GetManApplication.instance.navigateToRegister()
    }

    companion object {
        const val FXML_VIEW_NAME = "login-page.fxml"
    }
}