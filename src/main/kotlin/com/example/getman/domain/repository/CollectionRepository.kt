package com.example.getman.domain.repository

import com.example.getman.domain.model.CollectionModel
import com.example.getman.ui.main.model.JsonCollection

interface CollectionRepository {
    suspend fun add(collection: CollectionModel)
    suspend fun add(jsonCollection: JsonCollection, userId: Int)
    suspend fun getAll(userId: Int): List<CollectionModel>
    suspend fun deleteCollection(collectionId: Int)
    suspend fun deleteCollectionRequest(collectionId: Int, requestId: Int)
}