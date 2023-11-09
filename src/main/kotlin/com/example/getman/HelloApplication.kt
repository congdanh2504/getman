package com.example.getman

import com.example.getman.controllers.LoginController
import com.example.getman.utils.Navigator
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.context.startKoin
import org.kordamp.bootstrapfx.BootstrapFX

class HelloApplication : Application(), Navigator {
    private lateinit var job: Job
    private lateinit var primaryStage: Stage
    val applicationScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + job)

    override fun init() {
        super.init()
        instance = this
        startKoin {
            modules(appModule)
        }
    }

    override fun start(stage: Stage) {
        job = Job()
        primaryStage = stage
        navigateToLogin()
    }

    override fun navigateToLogin() {
        loadFxml(LoginController.FXML_VIEW_NAME, 320.0, 240.0, "Login")
    }

    override fun navigateToHome() {
        loadFxml(HelloController.FXML_VIEW_NAME, 700.0, 500.0, "Hello!")
    }

    override fun navigateToRegister() {

    }

    private fun loadFxml(fileName: String, width: Double, height: Double, title: String) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource(fileName))
        val scene = Scene(fxmlLoader.load(), width, height)
        scene.stylesheets.add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.title = title
        primaryStage.scene = scene
        primaryStage.show()
    }

    override fun stop() {
        super.stop()
        job.cancel()
    }

    companion object {
        lateinit var instance: HelloApplication
            private set
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}