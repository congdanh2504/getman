package com.example.getman

import com.example.getman.ui.login.LoginScreen
import com.example.getman.ui.register.RegisterScreen
import com.example.getman.di.appModule
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.ui.main.MainScreen
import com.example.getman.utils.Navigator
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.kordamp.bootstrapfx.BootstrapFX

class GetManApplication : Application(), Navigator, KoinComponent {
    lateinit var primaryStage: Stage
    val applicationScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val localRepository: LocalRepository by inject()

    override fun init() {
        super.init()
        instance = this
        startKoin {
            modules(appModule)
        }
    }

    override fun start(stage: Stage) {
        primaryStage = stage
        applicationScope.launch {
            val localUser = localRepository.getUser()
            if (localUser != null) {
                navigateToHome()
            } else {
                navigateToLogin()
            }
        }
    }

    override fun navigateToLogin() {
        loadFxml(LoginScreen.FXML_VIEW_NAME, "Login")
    }

    override fun navigateToHome() {
        loadFxml(MainScreen.FXML_VIEW_NAME, "Main")
    }

    override fun navigateToRegister() {
        loadFxml(RegisterScreen.FXML_VIEW_NAME, "Register")
    }

    private fun loadFxml(fileName: String, width: Double, height: Double, title: String) {
        val fxmlLoader = FXMLLoader(GetManApplication::class.java.getResource(fileName))
        val scene = Scene(fxmlLoader.load(), width, height)
        scene.stylesheets.add(BootstrapFX.bootstrapFXStylesheet())
        scene.stylesheets.add(GetManApplication::class.java.getResource("StyleSheet.css")?.toExternalForm())
        primaryStage.title = title
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun loadFxml(fileName: String, title: String) {
        val fxmlLoader = FXMLLoader(GetManApplication::class.java.getResource(fileName))
        val scene = Scene(fxmlLoader.load())
        scene.stylesheets.add(BootstrapFX.bootstrapFXStylesheet())
        scene.stylesheets.add(GetManApplication::class.java.getResource("StyleSheet.css")?.toExternalForm())
        primaryStage.title = title
        primaryStage.scene = scene
        primaryStage.show()
    }

    fun getStyleSheet(fileName: String): String? {
        return GetManApplication::class.java.getResource(fileName)?.toExternalForm()
    }

    fun loadFxml(fileName: String): FXMLLoader {
        return FXMLLoader(GetManApplication::class.java.getResource(fileName))
    }

    companion object {
        lateinit var instance: GetManApplication
            private set
    }
}

fun main() {
    Application.launch(GetManApplication::class.java)
}