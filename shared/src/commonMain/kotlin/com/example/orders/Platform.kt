package com.example.orders

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform