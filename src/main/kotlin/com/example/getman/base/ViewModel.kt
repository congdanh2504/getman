package com.example.getman.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class ViewModel {
    val job = Job()
    val viewModelScope = CoroutineScope(Dispatchers.IO + job)
}