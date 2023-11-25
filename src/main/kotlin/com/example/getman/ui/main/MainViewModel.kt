package com.example.getman.ui.main

import com.example.getman.base.ViewModel
import com.example.getman.domain.repository.NetworkRepository
import com.example.getman.utils.RequestEnum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import okhttp3.RequestBody
import okhttp3.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val networkRepository: NetworkRepository) : ViewModel() {

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

    fun request(request: RequestModel) = _requestTrigger.tryEmit(request)
}

data class RequestModel(
    val requestType: RequestEnum,
    val url: String,
    val headers: Map<String, String>,
    val body: RequestBody
)