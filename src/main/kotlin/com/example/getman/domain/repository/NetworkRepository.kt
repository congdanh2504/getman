package com.example.getman.domain.repository

import okhttp3.RequestBody
import okhttp3.Response

interface NetworkRepository {
    suspend fun get(url: String, headers: Map<String, String>): Response
    suspend fun post(url: String, body: RequestBody, headers: Map<String, String>): Response
    suspend fun put(url: String, body: RequestBody, headers: Map<String, String>): Response
    suspend fun patch(url: String, body: RequestBody, headers: Map<String, String>): Response
    suspend fun delete(url: String, headers: Map<String, String>): Response
    suspend fun head(url: String, headers: Map<String, String>): Response
    suspend fun options(url: String, headers: Map<String, String>): Response
}