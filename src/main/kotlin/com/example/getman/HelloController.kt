package com.example.getman

import com.example.getman.utils.applicationScope
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject

class HelloController : KoinComponent {
    @FXML
    private lateinit var welcomeText: Label

    private val network: Network by inject()
    private val responseFlow = MutableStateFlow<Response?>(null)

    init {
        applicationScope.launch {
            responseFlow.filterNotNull().collectLatest {
                welcomeText.text = it.body?.string()
            }
        }
    }

    @FXML
    private fun onHelloButtonClick() {
        applicationScope.launch {
            network.fetch("https://jsonplaceholder.typicode.com/todos/1").collect {
                responseFlow.emit(it)
            }
        }
    }

    fun handleLogin(actionEvent: ActionEvent) {

    }

    fun handleRegister(actionEvent: ActionEvent) {

    }

    companion object {
        const val FXML_VIEW_NAME = "hello-view.fxml"
    }
}