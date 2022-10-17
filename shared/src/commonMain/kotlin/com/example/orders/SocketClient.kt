package com.example.orders

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SocketClient {
    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 500
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    val sendMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    suspend fun setup() = flow {
            client.webSocket(host = "192.168.5.185", port = 8080, path = "/orders") {

                launch {
                    sendMessage.collect { message ->
                        message?.let { m ->
                            send(m)
                        }
                    }
                }

                for(message in incoming) {
                    if (message as? Frame.Text != null) {
                        emit(message.readText())
                    }
                }
            }
        }

}
