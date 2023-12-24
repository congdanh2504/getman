package com.example.getman.ui.main.model

import com.example.getman.domain.model.CollectionModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.RequestEnum

data class JsonCollection(
    val name: String,
    val requests: List<JsonRequest>
)

fun CollectionModel.toJsonCollection(): JsonCollection =
    JsonCollection(
        name = name,
        requests = requests.map {
            JsonRequest(
                requestType = it.requestType,
                url = it.url,
                params = it.params,
                headers = it.headers,
                bodyType = it.bodyType,
                formData = it.formData,
                body = it.body
            )
        }
    )

data class JsonRequest(
    val requestType: RequestEnum,
    val url: String,
    val params: List<KeyValueTableModel> = emptyList(),
    val headers: List<KeyValueTableModel> = emptyList(),
    val bodyType: BodyEnum = BodyEnum.NONE,
    val formData: List<KeyValueTableModel> = emptyList(),
    val body: String? = null
)