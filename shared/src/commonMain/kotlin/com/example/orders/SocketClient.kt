package com.example.orders

import com.example.orders.config.Configuration
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SocketClient(
    private val configuration: Configuration
) {
    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 500
        }
    }

    val sendMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    suspend fun setup() = flow {
            client.webSocket(host = configuration.socketBaseUrl, port = 8080, path = "/orders") {

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
