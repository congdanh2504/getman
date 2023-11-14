package com.example.getman.ui.login

import com.example.getman.GetManApplication
import com.example.getman.base.Screen
import com.example.getman.extensions.collectIn
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.koin.core.inject

class LoginScreen : Screen() {

    lateinit var registerButton: Button
    lateinit var loginButton: Button
    lateinit var passwordField: PasswordField
    lateinit var emailField: TextField
    override val viewModel by inject<LoginViewModel>()

    override fun onCreate() {
        super.onCreate()
        viewModel.loginState.collectIn(this) { state ->
            when (state) {
                is LoginUiState.Success -> {
                    viewModel.saveUserToLocal(state.user)
                }

                is LoginUiState.Error -> {
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Error"
                        contentText = state.message
                    }.show()
                }

                else -> {}
            }
        }
        viewModel.saveUserState.collectIn(this) {
            if (it) {
                GetManApplication.instance.navigateToHome()
                onDestroy()
            }
        }
    }

    fun handleLogin() {
        viewModel.login(emailField.text, passwordField.text)
    }

    fun handleRegister() {
        GetManApplication.instance.navigateToRegister()
        onDestroy()
    }

    companion object {
        const val FXML_VIEW_NAME = "login-page.fxml"
    }
}