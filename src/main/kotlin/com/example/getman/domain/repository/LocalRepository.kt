package com.example.getman.domain.repository

import com.example.getman.domain.model.User

interface LocalRepository {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun removeUser()
}