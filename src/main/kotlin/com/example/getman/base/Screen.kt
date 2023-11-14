package com.example.getman.base

import kotlinx.coroutines.*
import org.koin.core.KoinComponent

abstract class Screen : KoinComponent, LifeCycle {

    private val job: Job = Job()
    val screenScope = CoroutineScope(Dispatchers.Main + job)
    abstract val viewModel: ViewModel

    init {
        screenScope.launch {
            delay(1000)
            this@Screen.onCreate()
        }
    }

    override fun onCreate() {}

    override fun onDestroy() {
        viewModel.job.cancel()
        job.cancel()
    }
}