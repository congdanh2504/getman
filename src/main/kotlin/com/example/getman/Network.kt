package com.example.getman

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class Network(private val client: OkHttpClient) {

    fun fetch(url: String): Flow<Response> = flow {
        val request = Request.Builder()
            .url(url)
            .build()
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
        emit(response)
    }
}