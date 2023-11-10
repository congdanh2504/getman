package com.example.getman.data.local.repository

import com.example.getman.data.local.datasource.LocalDataSource
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.LocalRepository

class LocalRepositoryImpl(private val localDataSource: LocalDataSource): LocalRepository {
    override suspend fun saveUser(user: User) = localDataSource.saveUserToFile(user)

    override suspend fun getUser(): User? = localDataSource.readUserFromFile()

    override suspend fun removeUser() = localDataSource.removeUserFromFile()
}