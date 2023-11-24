package com.example.getman.ui.main

import com.example.getman.base.ViewModel
import com.example.getman.domain.repository.NetworkRepository
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
                emit(networkRepository.get(it.url, it.headers))
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun request(request: RequestModel) = _requestTrigger.tryEmit(request)
}

data class RequestModel(
    val url: String,
    val headers: Map<String, String>,
    val body: RequestBody? = null
)