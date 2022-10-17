package com.example.orders.di

import com.example.orders.SocketClient
import com.example.orders.config.Configuration
import com.example.orders.config.ConfigurationProd
import com.example.orders.config.ConfigurationStage
import io.ktor.util.*
import org.koin.dsl.module

fun appModule() = listOf(commonModule)

val commonModule = module {
    single {
        if(PlatformUtils.IS_DEVELOPMENT_MODE) ConfigurationProd
        else ConfigurationStage
    }
    single { SocketClient(get()) }
}
