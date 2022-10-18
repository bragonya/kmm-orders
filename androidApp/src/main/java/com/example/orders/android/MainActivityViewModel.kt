package com.example.orders.android

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orders.SocketClient
import kotlinx.coroutines.launch

class MainActivityViewModel(
    val socketClient: SocketClient
): ViewModel() {

    val messages = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            socketClient.setup(this).collect { message ->
                messages.add(message)
            }
        }
    }

    fun sendMessage(message: String) {
        socketClient.sendMessage.value = message
    }
}
