package com.example.orders.android.di

import com.example.orders.android.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val androidModule = module {
    viewModel { MainActivityViewModel(get()) }
}
