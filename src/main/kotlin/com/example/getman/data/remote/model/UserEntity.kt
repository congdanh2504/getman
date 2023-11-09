package com.example.getman.data.remote.model

import com.example.getman.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "User")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,
    @Column(name = "username")
    val username: String = "",
    @Column(name = "email")
    val email: String = "",
    @Column(name = "password")
    val password: String = ""
)

fun User.toUserEntity() = UserEntity(
    username = username,
    email = email,
    password = password
)