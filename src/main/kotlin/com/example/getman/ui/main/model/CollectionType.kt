package com.example.getman.ui.main.model

sealed interface CollectionType {
    data class CollectionTitle(val id: Int, val name: String) : CollectionType
    data class CollectionRequest(val collectionId: Int, val request: RequestModel) : CollectionType
}