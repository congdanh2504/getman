package com.example.getman.ui.main

import com.example.getman.base.ViewModel
import com.example.getman.domain.model.CollectionModel
import com.example.getman.domain.repository.CollectionRepository
import com.example.getman.domain.repository.HistoryRepository
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.domain.repository.NetworkRepository
import com.example.getman.ui.main.model.HistoryRequest
import com.example.getman.ui.main.model.JsonCollection
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.RequestEnum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File

class MainViewModel(
    private val localRepository: LocalRepository,
    private val historyRepository: HistoryRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _historyRequest = MutableStateFlow<List<HistoryRequest>>(emptyList())
    val historyRequest = _historyRequest.asStateFlow()

    private val _collections = MutableStateFlow<List<CollectionModel>>(emptyList())
    val collections = _collections.asStateFlow()

    var isAdding = false
    var addingCollectionId: Int? = null

    fun getHistoryRequests() = viewModelScope.launch {
        val user = localRepository.getUser()
        user?.let {
            val requests = historyRepository.getHistoryRequests(it.id)
            _historyRequest.emit(requests)
        }
    }

    fun getCollections() = viewModelScope.launch {
        val user = localRepository.getUser()
        user?.let {
            val collections = collectionRepository.getAll(it.id)
            _collections.emit(collections)
        }
    }

    fun addCollection(name: String) = viewModelScope.launch {
        val user = localRepository.getUser()
        user?.let {
            collectionRepository.add(
                CollectionModel(
                    name = name,
                    userId = it.id
                )
            )
            getCollections()
        }
    }

    fun addCollection(jsonCollection: JsonCollection) = viewModelScope.launch {
        val user = localRepository.getUser()
        user?.let {
            collectionRepository.add(jsonCollection, it.id)
            getCollections()
        }
    }

    fun deleteCollection(collectionId: Int) = viewModelScope.launch {
        collectionRepository.deleteCollection(collectionId)
        getCollections()
    }

    fun deleteCollectionRequest(collectionId: Int, requestId: Int) = viewModelScope.launch {
        collectionRepository.deleteCollectionRequest(collectionId, requestId)
        getCollections()
    }

    fun removeUser() = viewModelScope.launch {
        localRepository.removeUser()
    }

    fun generateTestcases(filename: String): List<Map<String, String>> {
        val parameters = mutableMapOf<String, List<String>>()
        val constraints: MutableList<Constraint> = mutableListOf()
        File(filename).forEachLine {
            if (it.startsWith("IF")) {
                val parts = it.split("IF ", " = ", " THEN ", " = ")

                val firstKey = parts[1].replace("[", "").replace("]", "")
                val firstValue = parts[2].replace("\"", "")
                val secondKey = parts[3].replace("[", "").replace("]", "")
                val secondValue = parts[4].replace("{", "").replace("}", "").split(", ")

                if (!parameters[firstKey]!!.contains(firstValue)) {
                    throw Exception()
                }

                secondValue.forEach { value ->
                    if (!parameters[secondKey]!!.contains(value)) {
                        throw Exception()
                    }
                }

                val constraint: Constraint = Pair(Pair(firstKey, firstValue), Pair(secondKey, secondValue))
                constraints.add(constraint)
                return@forEachLine
            }
            val splitKeyValue = it.split(": ")
            val key = splitKeyValue[0]
            val value = splitKeyValue[1].split(", ")
            parameters[key] = value
        }

        val testCases: List<Map<String, String>> = generateTestCases(parameters)

        val newTest: List<Map<String, String>> = testCases.mapNotNull { test ->
            constraints.forEach { constraint ->
                if (test[constraint.first.first] == constraint.first.second) {
                    if (!constraint.second.second.contains(test[constraint.second.first])) {
                        return@mapNotNull null
                    }
                }
            }
            test
        }
        return newTest
    }

    private fun generateTestCases(parameters: Map<String, List<String>>): List<Map<String, String>> {
        val testCases = mutableListOf<Map<String, String>>()
        generateTestCasesHelper(parameters, mutableMapOf(), testCases)
        return testCases
    }

    private fun generateTestCasesHelper(
        parameters: Map<String, List<String>>,
        current: MutableMap<String, String>,
        testCases: MutableList<Map<String, String>>
    ) {
        if (current.size == parameters.size) {
            testCases.add(current.toMap())
            return
        }

        for ((key, value) in parameters) {
            if (!current.containsKey(key)) {
                for (v in value) {
                    current[key] = v
                    generateTestCasesHelper(parameters, current, testCases)
                    current.remove(key)
                }
                break
            }
        }
    }
}

data class RequestModel(
    val requestType: RequestEnum,
    val url: String,
    val headers: Map<String, String>,
    val body: RequestBody
)