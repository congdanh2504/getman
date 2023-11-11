package com.example.getman.controllers

import com.example.getman.GetManApplication
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.UserRepository
import com.example.getman.utils.applicationScope
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import kotlinx.coroutines.launch
import net.synedra.validatorfx.Validator
import org.koin.core.KoinComponent
import org.koin.core.inject

class RegisterController : KoinComponent {

    lateinit var registerButton: Button
    lateinit var rePasswordField: PasswordField
    lateinit var passwordField: PasswordField
    lateinit var emailField: TextField
    lateinit var usernameField: TextField

    private val userRepository: UserRepository by inject()

    fun handleRegister() {
        if (!validateData()) return
        applicationScope.launch {
            val userWithEmail = userRepository.getUserByEmail(emailField.text)
            if (userWithEmail != null) {
                Alert(AlertType.ERROR).apply {
                    title = "Error"
                    contentText = "Email is used by another user"
                }.also {
                    it.show()
                }
            } else {
                val user = User(
                    username = usernameField.text,
                    email = emailField.text,
                    password = passwordField.text
                )
                if (userRepository.saveUser(user)) {
                    Alert(AlertType.INFORMATION).apply {
                        title = "Successfully"
                        contentText = "Register successfully"
                    }.also {
                        it.show()
                        it.setOnHidden {
                            GetManApplication.instance.navigateToLogin()
                        }
                    }
                } else {
                    Alert(AlertType.ERROR).apply {
                        title = "Error"
                        contentText = "Register error"
                    }.also {
                        it.show()
                    }
                }
            }
        }
    }

    private fun validateData(): Boolean {
        val validator = Validator()

        validator.createCheck()
            .dependsOn("username", usernameField.textProperty())
            .withMethod {
                val username = it.get<String>("username")
                if (username.isEmpty()) {
                    it.error("Username is required")
                }
            }
            .decorates(usernameField)
            .immediate()

        validator.createCheck()
            .dependsOn("email", emailField.textProperty())
            .withMethod {
                val email = it.get<String>("email")
                val emailRegex = "^[A-Za-z\\d+_.-]+@(.+)$".toRegex()
                if (email.isEmpty()) {
                    it.error("Email is required")
                } else if (!emailRegex.matches(email)) {
                    it.error("Invalid email format")
                }
            }
            .decorates(emailField)
            .immediate()

        validator.createCheck()
            .dependsOn("password", passwordField.textProperty())
            .withMethod {
                val password = it.get<String>("password")
                if (password.isEmpty()) {
                    it.error("Password is required")
                }
            }
            .decorates(usernameField)
            .immediate()

        validator.createCheck()
            .dependsOn("password", passwordField.textProperty())
            .dependsOn("rePassword", rePasswordField.textProperty())
            .withMethod {
                val password = it.get<String>("password")
                val rePassword = it.get<String>("rePassword")
                if (rePassword.isEmpty()) {
                    it.error("Password confirmation is required")
                } else if (password != rePassword) {
                    it.error("Passwords do not match")
                }
            }
            .decorates(rePasswordField)
            .immediate()

        return validator.validate()
    }

    fun handleCancel() {
        GetManApplication.instance.navigateToLogin()
    }

    companion object {
        const val FXML_VIEW_NAME = "register-page.fxml"
    }
}