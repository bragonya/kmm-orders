package com.example.orders.config

object ConfigurationProd: Configuration {
    override val socketBaseUrl: String = if(false) "192.168.0.11" else "192.168.5.185"
    override val port: Int = 8083
}

