package com.example.getman.data.remote.model

import jakarta.persistence.*

@Entity
@Table(name = "Request")
data class RequestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestId")
    val requestId: Int = 0,
    @Column(name = "url")
    val url: String = "",
    @Column(name = "requestType")
    val requestType: String = "",
    @Column(name = "bodyId")
    val bodyId: Int = 0
)