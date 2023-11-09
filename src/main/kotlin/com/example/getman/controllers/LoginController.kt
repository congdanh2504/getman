package com.example.getman.controllers

import com.example.getman.HelloApplication
import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField

class LoginController {

    lateinit var registerButton: Button
    lateinit var loginButton: Button
    lateinit var passwordField: PasswordField
    lateinit var usernameField: TextField

    fun handleLogin(actionEvent: ActionEvent) {
        HelloApplication.instance.navigateToHome()
    }

    fun handleRegister(actionEvent: ActionEvent) {

    }

    companion object {
        const val FXML_VIEW_NAME = "login-page.fxml"
    }
}