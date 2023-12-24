package com.example.getman.data.remote.repository

import com.example.getman.data.remote.datasource.HistoryDao
import com.example.getman.domain.repository.HistoryRepository
import com.example.getman.ui.main.model.HistoryRequest
import com.example.getman.ui.main.model.RequestModel

class HistoryRepositoryImpl(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override suspend fun getHistoryRequests(userId: Int): List<HistoryRequest> {
        return historyDao.getHistoryRequests(userId)
    }

    override suspend fun add(requestModel: RequestModel, userId: Int, isAddingToCollection: Boolean, collectionId: Int?) {
        return historyDao.add(requestModel, userId, isAddingToCollection, collectionId)
    }
}