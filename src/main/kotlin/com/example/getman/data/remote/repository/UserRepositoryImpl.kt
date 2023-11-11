package com.example.getman.data.remote.repository

import com.example.getman.data.remote.datasource.UserDao
import com.example.getman.data.remote.model.toUserEntity
import com.example.getman.domain.model.User
import com.example.getman.domain.repository.UserRepository
import com.example.getman.utils.PasswordEncryptor

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun saveUser(user: User) =
        userDao.saveUser(user.copy(password = PasswordEncryptor.encryptPassword(user.password ?: "")).toUserEntity())

    override suspend fun login(email: String, password: String): User? =
        userDao.getUserByEmailAndPassword(email, PasswordEncryptor.encryptPassword(password))

    override suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}