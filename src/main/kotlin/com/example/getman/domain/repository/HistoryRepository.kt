package com.example.getman.domain.repository

import com.example.getman.ui.main.model.HistoryRequest
import com.example.getman.ui.main.model.RequestModel

interface HistoryRepository {
    suspend fun getHistoryRequests(userId: Int): List<HistoryRequest>
    suspend fun add(requestModel: RequestModel, userId: Int, isAddingToCollection: Boolean = false, collectionId: Int? = null)
}