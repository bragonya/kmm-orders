package com.example.orders.android

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orders.SocketClient
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    val messages = mutableStateListOf<String>()
    private val socketClient = SocketClient()
    init {
        viewModelScope.launch {
            socketClient.setup().collect { message ->
                messages.add(message)
            }
        }
    }

    fun sendMessage(message: String) {
        socketClient.sendMessage.value = message
    }
}
