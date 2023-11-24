package com.example.getman.base

import javafx.fxml.Initializable
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import java.net.URL
import java.util.*

abstract class Screen : KoinComponent, LifeCycle, Initializable {

    private val job: Job = Job()
    val screenScope = CoroutineScope(Dispatchers.Main + job)
    abstract val viewModel: ViewModel

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        onCreate()
    }

    override fun onCreate() {}

    override fun onDestroy() {
        viewModel.job.cancel()
        job.cancel()
    }
}