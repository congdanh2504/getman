package com.example.getman.ui.main

import com.example.getman.base.ViewModel
import com.example.getman.domain.repository.HistoryRepository
import com.example.getman.domain.repository.LocalRepository
import com.example.getman.domain.repository.NetworkRepository
import com.example.getman.main
import com.example.getman.ui.main.model.HistoryRequest
import com.example.getman.utils.RequestEnum
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Response

class TabViewModel(
    private val mainViewModel: MainViewModel,
    private val networkRepository: NetworkRepository,
    private val localRepository: LocalRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val response: StateFlow<Response?>

    private val _requestTrigger = MutableSharedFlow<RequestModel>(extraBufferCapacity = Int.MAX_VALUE)

    init {
        response = _requestTrigger.flatMapLatest {
            flow {
                when (it.requestType) {
                    RequestEnum.GET -> emit(networkRepository.get(it.url, it.headers))
                    RequestEnum.POST -> emit(networkRepository.post(it.url, it.body, it.headers))
                    RequestEnum.PUT -> emit(networkRepository.put(it.url, it.body, it.headers))
                    RequestEnum.PATCH -> emit(networkRepository.patch(it.url, it.body, it.headers))
                    RequestEnum.DELETE -> emit(networkRepository.delete(it.url, it.headers))
                    RequestEnum.HEAD -> emit(networkRepository.delete(it.url, it.headers))
                    RequestEnum.OPTION -> emit(networkRepository.options(it.url, it.headers))
                }
            }.catch {
                println("Error: ${it.message}")
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun addHistory(request: com.example.getman.ui.main.model.RequestModel) = viewModelScope.launch {
        val user = localRepository.getUser()
        user?.let {
            val job = launch {
                historyRepository.add(request, it.id, mainViewModel.isAdding, mainViewModel.addingCollectionId)
                mainViewModel.isAdding = false
            }
            job.join()
            mainViewModel.getHistoryRequests()
            mainViewModel.getCollections()
        }
    }

    fun request(request: RequestModel) = _requestTrigger.tryEmit(request)
}