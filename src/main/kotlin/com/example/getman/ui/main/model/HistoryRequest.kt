package com.example.getman.ui.main.model

import java.sql.Timestamp

data class HistoryRequest(
    val time: Timestamp, val request: RequestModel
)

sealed interface HistoryType {
    data class HistoryDate(val date: String) : HistoryType
    data class HistoryRequest(val request: RequestModel) : HistoryType
}
