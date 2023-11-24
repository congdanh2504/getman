package com.example.getman.di

import com.example.getman.data.local.datasource.LocalDataSource
import com.example.getman.data.local.repository.LocalRepositoryImpl
import com.example.getman.data.remote.datasource.UserDao
import com.example.getman.data.remote.repository.NetworkRepositoryIml
import com.example.getman.data.remote.repository.UserRepositoryImpl
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.domain.repository.NetworkRepository
import com.example.getman.domain.repository.UserRepository
import com.example.getman.ui.login.LoginViewModel
import com.example.getman.ui.main.MainViewModel
import com.example.getman.ui.register.RegisterViewModel
import okhttp3.OkHttpClient
import org.koin.dsl.module

private val viewModelModule = module {
    factory { LoginViewModel(get(), get()) }
    factory { RegisterViewModel(get()) }
    factory { MainViewModel(get()) }
}

private val repositoryModule = module {
    single<LocalRepository> { LocalRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NetworkRepository> { NetworkRepositoryIml(get()) }
}

private val serviceModule = module {
    single { UserDao() }
    single { LocalDataSource() }
    single { OkHttpClient() }
}

val appModule = listOf(viewModelModule, repositoryModule, serviceModule)