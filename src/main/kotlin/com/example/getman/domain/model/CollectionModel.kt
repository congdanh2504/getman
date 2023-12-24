package com.example.getman.domain.model

import com.example.getman.ui.main.model.RequestModel

data class CollectionModel(
    val id: Int? = null,
    val name: String,
    val userId: Int,
    val requests: MutableList<RequestModel> = mutableListOf()
)