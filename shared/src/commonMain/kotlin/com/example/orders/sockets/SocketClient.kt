package com.example.orders.sockets

import com.example.orders.config.Configuration
import com.example.orders.models.SendOrder
import com.example.orders.serialization.encodeToPayload
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.ktor.client.RSocketSupport
import io.rsocket.kotlin.ktor.client.rSocket
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.metadata
import io.rsocket.kotlin.metadata.toPacket
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.tcp.TcpClientTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.subscribeOn
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
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
                        metadata = WellKnownMimeType.MessageRSocketRouting
                    )
                }
            }
        }
    }



    val sendMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalSerializationApi::class, ExperimentalMetadataApi::class)
    suspend fun setup(coroutineScope: CoroutineScope) = flow {
        try {
            val rSocket: RSocket = client.rSocket(host = configuration.socketBaseUrl, port = configuration.port)

            coroutineScope.launch {
                rSocket.requestChannel(
                    buildPayload {
                        data("""{"data":""}""")
                        metadata(RoutingMetadata("post"))
                    },
                    sendMessage.mapNotNull { message ->
                        message?.let {
                            println("bragonya sending message")
                            buildPayload {
                                data(ProtoBuf.encodeToByteArray(Response(message)))
                                metadata(RoutingMetadata("post"))
                            }
                        }
                    }
                ).collect()
            }

            println("bragonya listening")
            rSocket.requestStream(
                buildPayload {
                    data("brayan")
                    metadata(
                        RoutingMetadata("stream")
                    )
                }
            ).collect {  payload ->
                emit(SocketState.Success(payload.data.readText()))
            }

            println("bragonya End")
        } catch (e: Exception) {
            emit(SocketState.Failure(e))
        }
    }
}

@Serializable
data class Response(val data: String)
