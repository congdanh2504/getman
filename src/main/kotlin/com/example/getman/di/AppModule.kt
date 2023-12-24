package com.example.getman.di

import com.example.getman.data.local.datasource.LocalDataSource
import com.example.getman.data.local.repository.LocalRepositoryImpl
import com.example.getman.data.remote.datasource.CollectionDao
import com.example.getman.data.remote.datasource.HistoryDao
import com.example.getman.data.remote.datasource.UserDao
import com.example.getman.data.remote.repository.CollectionRepositoryImpl
import com.example.getman.data.remote.repository.HistoryRepositoryImpl
import com.example.getman.data.remote.repository.NetworkRepositoryIml
import com.example.getman.data.remote.repository.UserRepositoryImpl
import com.example.getman.domain.repository.*
import com.example.getman.ui.login.LoginViewModel
import com.example.getman.ui.main.MainViewModel
import com.example.getman.ui.main.TabViewModel
import com.example.getman.ui.register.RegisterViewModel
import okhttp3.OkHttpClient
import org.koin.dsl.module

private val viewModelModule = module {
    factory { LoginViewModel(get(), get()) }
    factory { RegisterViewModel(get()) }
    single { MainViewModel(get(), get(), get()) }
    factory { TabViewModel(get(), get(), get(), get()) }
}

private val repositoryModule = module {
    single<LocalRepository> { LocalRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NetworkRepository> { NetworkRepositoryIml(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get()) }
}

private val serviceModule = module {
    single { UserDao() }
    single { HistoryDao() }
    single { LocalDataSource() }
    single { OkHttpClient() }
    single { CollectionDao() }
}

val appModule = listOf(viewModelModule, repositoryModule, serviceModule)