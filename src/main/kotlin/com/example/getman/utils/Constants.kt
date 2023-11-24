package com.example.getman.utils

object Constants {
    const val URL_REGEX_STRING = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"
    val commonHeaders = mutableMapOf<String, List<String>>()

    init {
        commonHeaders["Accept"] = listOf("text/plain", "application/json", "*/*")
        commonHeaders["Authorization"] = listOf("Bearer {token}", "Basic {credentials}")
        commonHeaders["Cache-Control"] = listOf("no-cache", "no-store", "max-age={seconds}")
        commonHeaders["Content-Type"] = listOf("application/json", "text/html", "multipart/form-data")
    }
}