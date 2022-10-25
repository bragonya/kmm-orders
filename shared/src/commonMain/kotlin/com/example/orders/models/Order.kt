package com.example.orders.models


import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable;

interface OrderApi {
    suspend fun send(chatId: Int, content: String): Order
    suspend fun history(chatId: Int, limit: Int = 10): List<Order>
    fun messages(chatId: Int, fromMessageId: Int): Flow<Order>
}

@Serializable
data class Order(
    val id: Int,
    val orderId: Int,
    val senderId: Int,
    val timestamp: Long,
    val content: String,
)

@Serializable
data class SendOrder(val content: String)

@Serializable
data class HistoryOrder(val limit: Int)

@Serializable
data class StreamMessages(val fromOrderId: Int)
