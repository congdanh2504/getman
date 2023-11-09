package com.example.getman

import okhttp3.OkHttpClient
import org.koin.dsl.module

val appModule = module {
    single { OkHttpClient() }
    single { Network(get()) }
}