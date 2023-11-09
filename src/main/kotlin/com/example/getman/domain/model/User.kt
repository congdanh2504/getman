package com.example.getman.domain.model

import com.example.getman.data.remote.model.UserEntity

data class User(
    val username: String,
    val email: String,
    val password: String
)