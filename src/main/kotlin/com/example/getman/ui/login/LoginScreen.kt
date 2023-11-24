package com.example.getman.ui.login

import com.example.getman.GetManApplication
import com.example.getman.base.Screen
import com.example.getman.extensions.collectIn
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.koin.core.inject
import kotlin.math.log

class LoginScreen : Screen() {

    lateinit var registerButton: Button
    lateinit var loginButton: Button
    lateinit var passwordField: PasswordField
    lateinit var emailField: TextField
    override val viewModel by inject<LoginViewModel>()

    override fun onCreate() {
        super.onCreate()
        initListeners()
        bindViewModel()
    }

    private fun initListeners() {
        loginButton.setOnAction {
            viewModel.login(emailField.text, passwordField.text)
        }
        registerButton.setOnAction {
            GetManApplication.instance.navigateToRegister()
            onDestroy()
        }
    }

    private fun bindViewModel() = viewModel.run {
        loginState.collectIn(this@LoginScreen, ::handleLoginState)
        saveUserState.collectIn(this@LoginScreen, ::handleSaveUserState)
    }

    private fun handleLoginState(state: LoginUiState) {
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

    private fun handleSaveUserState(isSaved: Boolean) {
        if (isSaved) {
            GetManApplication.instance.navigateToHome()
            onDestroy()
        }
    }

    companion object {
        const val FXML_VIEW_NAME = "login-page.fxml"
    }
}