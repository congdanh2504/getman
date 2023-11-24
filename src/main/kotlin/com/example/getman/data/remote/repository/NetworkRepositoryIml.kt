package com.example.getman.data.remote.repository

import com.example.getman.domain.repository.NetworkRepository
import com.example.getman.utils.RequestEnum
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class NetworkRepositoryIml(private val client: OkHttpClient) : NetworkRepository {

    override suspend fun get(url: String, headers: Map<String, String>): Response {
        return request(RequestEnum.GET.value, url, headers = headers)
    }

    override suspend fun post(url: String, body: RequestBody, headers: Map<String, String>): Response {
        return request(RequestEnum.POST.value, url, body, headers)
    }

    override suspend fun put(url: String, body: RequestBody, headers: Map<String, String>): Response {
        return request(RequestEnum.PUT.value, url, body, headers)
    }

    override suspend fun patch(url: String, body: RequestBody, headers: Map<String, String>): Response {
        return request(RequestEnum.PATCH.value, url, body, headers)
    }

    override suspend fun delete(url: String, headers: Map<String, String>): Response {
        return request(RequestEnum.DELETE.value, url, headers = headers)
    }

    override suspend fun head(url: String, headers: Map<String, String>): Response {
        return request(RequestEnum.HEAD.value, url, headers = headers)
    }

    override suspend fun options(url: String, headers: Map<String, String>): Response {
        return request(RequestEnum.OPTION.value, url, headers = headers)
    }

    private fun request(
        method: String,
        url: String,
        body: RequestBody? = null,
        headers: Map<String, String>? = null
    ): Response {
        val requestBuilder = Request.Builder()
            .url(url)
            .method(method, body)

        headers?.let {
            for ((key, value) in it) {
                requestBuilder.addHeader(key, value)
            }
        }

        val request = requestBuilder.build()
        return client.newCall(request).execute()
    }
}