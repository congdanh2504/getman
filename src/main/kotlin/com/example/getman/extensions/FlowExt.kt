package com.example.getman.extensions

import com.example.getman.base.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectIn(screen: Screen, action: suspend (T) -> Unit) {
    screen.screenScope.launch {
        collect {
            action(it)
        }
    }
}