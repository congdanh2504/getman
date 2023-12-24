package com.example.getman.data.remote.repository

import com.example.getman.data.remote.datasource.CollectionDao
import com.example.getman.domain.model.CollectionModel
import com.example.getman.domain.repository.CollectionRepository
import com.example.getman.ui.main.model.JsonCollection

class CollectionRepositoryImpl(
    private val collectionDao: CollectionDao
) : CollectionRepository {
    override suspend fun add(collection: CollectionModel) {
        return collectionDao.add(collection)
    }

    override suspend fun add(jsonCollection: JsonCollection, userId: Int) {
        return collectionDao.add(jsonCollection, userId)
    }

    override suspend fun getAll(userId: Int): List<CollectionModel> {
        return collectionDao.getAll(userId)
    }

    override suspend fun deleteCollection(collectionId: Int) {
        return collectionDao.deleteCollection(collectionId)
    }

    override suspend fun deleteCollectionRequest(collectionId: Int, requestId: Int) {
        return collectionDao.deleteCollectionRequest(collectionId, requestId)
    }
}