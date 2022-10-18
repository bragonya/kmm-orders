package com.example.orders.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orders.sockets.SocketClient
import com.example.orders.sockets.SocketState
import kotlinx.coroutines.launch

class MainActivityViewModel(
    val socketClient: SocketClient
): ViewModel() {

    val messages = mutableStateListOf<String>()
    var errorScreen by mutableStateOf(false)
    init {
        viewModelScope.launch {
            socketClient.setup(this).collect { state ->
                when(state) {
                    is SocketState.Success -> {
                        if(errorScreen) {
                            errorScreen = false
                        }
                        messages.add(state.text)
                    }
                    is SocketState.Failure -> {
                        if(messages.isEmpty()) { errorScreen = true }
                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        socketClient.sendMessage.value = message
    }
}
