package com.example.getman.domain.repository

import com.example.getman.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Boolean

    suspend fun login(email: String, password: String): User?

    suspend fun getUserByEmail(email: String): User?
}