package com.example.orders.sockets

import com.example.orders.config.Configuration
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.ktor.client.RSocketSupport
import io.rsocket.kotlin.ktor.client.rSocket
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

class SocketClient(
    private val configuration: Configuration
) {
    @OptIn(ExperimentalTime::class)
    private val client = HttpClient {
        install(WebSockets) //rsocket requires websockets plugin installed
        install(RSocketSupport) {
            //configure rSocket connector (all values have defaults)
            connector {
                maxFragmentSize = 1024

                connectionConfig {
                    keepAlive = KeepAlive(
                        interval = 30.seconds,
                        maxLifetime = 2.minutes
                    )

                    //payload for setup frame
                    setupPayload {
                        buildPayload {
                            data("brayan")
                        }
                    }

                    //mime types
                    payloadMimeType = PayloadMimeType(
                        data = WellKnownMimeType.ApplicationJson,
                        metadata = WellKnownMimeType.MessageRSocketCompositeMetadata
                    )
                }

                //optional acceptor for server requests
                acceptor {
                    RSocketRequestHandler {
                        requestResponse { it } //echo request payload
                    }
                }
            }
        }
    }



    val sendMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    suspend fun setup(coroutineScope: CoroutineScope) = flow {
        try {
            val rSocket: RSocket = client.rSocket(configuration.socketBaseUrl)

            coroutineScope.launch {
                launch(SupervisorJob(this.coroutineContext.job)) {
                    sendMessage.collect { message ->
                        message?.let { m ->
                            println("bragonya trying to send message")
                            rSocket.fireAndForget(
                                Payload(
                                    data = ByteReadPacket(m.toByteArray())
                                )
                            )
                        }
                    }
                }
            }

            val stream: Flow<Payload> = rSocket.requestStream(
                buildPayload {
                    data("Brayan")
                }
            )

            stream.onEach {  payload ->
                emit(SocketState.Success(payload.data.readText()))
            }
        } catch (e: Exception) {
            emit(SocketState.Failure(e))
        }
    }
}
