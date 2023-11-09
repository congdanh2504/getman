package com.example.getman.di

import com.example.getman.Network
import com.example.getman.data.remote.datasource.UserDao
import com.example.getman.data.remote.repository.UserRepositoryImpl
import com.example.getman.domain.repository.UserRepository
import okhttp3.OkHttpClient
import org.koin.dsl.module

val appModule = module {
    single { UserDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { OkHttpClient() }
    single { Network(get()) }
}