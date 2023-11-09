package com.example.getman.data.remote.repository

import com.example.getman.data.remote.datasource.UserDao
import com.example.getman.data.remote.model.toUserEntity
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun saveUser(user: User) = userDao.saveUser(user.toUserEntity())
}