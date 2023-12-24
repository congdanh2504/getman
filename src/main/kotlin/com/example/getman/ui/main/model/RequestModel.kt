package com.example.getman.ui.main.model

import com.example.getman.utils.BodyEnum
import com.example.getman.utils.RequestEnum

data class RequestModel(
    val id : Int? = null,
    val requestType: RequestEnum,
    val url: String,
    val params: List<KeyValueTableModel> = emptyList(),
    val headers: List<KeyValueTableModel> = emptyList(),
    val bodyType: BodyEnum = BodyEnum.NONE,
    val formData: List<KeyValueTableModel> = emptyList(),
    val body: String? = null
)