package com.example.orders.sockets

sealed class SocketState {
    data class Success(val text: String): SocketState()
    data class Failure(val e: Throwable): SocketState()
}
