package com.example.getman

import javafx.fxml.FXML
import javafx.scene.control.Label

class ViewController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        HelloApplication.instance.navigateToHome()
    }

    companion object {
        const val FXML_VIEW_NAME = "view.fxml"
    }
}