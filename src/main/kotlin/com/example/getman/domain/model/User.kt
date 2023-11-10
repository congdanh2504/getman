package com.example.getman.domain.model

import java.io.Serializable

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String? = null
): Serializable