package com.example.orders.sockets

import com.example.orders.config.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient

class SocketClient(
    private val configuration: Configuration
) {
    private val client = StompClient(KtorWebSocketClient())

    val sendMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    suspend fun setup(coroutineScope: CoroutineScope) = flow {
        try {
            val session: StompSession = client.connect(configuration.socketBaseUrl)

            coroutineScope.launch {
                launch(SupervisorJob(this.coroutineContext.job)) {
                    sendMessage.collect { message ->
                        message?.let { m ->
                            session.sendText("/app/orders", m)
                        }
                    }
                }
            }
            val subscription: Flow<String> = session.subscribeText("/topic/orders")

            subscription.collect { msg ->
                emit(SocketState.Success(msg))
            }
        } catch (e: Exception) {
            emit(SocketState.Failure(e))
        }
    }
}
