package com.example.orders.di

import com.example.orders.config.Configuration
import com.example.orders.sockets.SocketClient
import com.example.orders.config.ConfigurationProd
import org.koin.dsl.module

fun appModule() = listOf(commonModule)

val commonModule = module {
    single<Configuration> {
        ConfigurationProd
    }
    single { SocketClient(get()) }
}
