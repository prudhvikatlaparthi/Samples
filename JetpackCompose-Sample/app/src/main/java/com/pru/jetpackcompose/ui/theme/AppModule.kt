package com.pru.jetpackcompose.ui.theme

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {
    val appModule = module {

        // single instance of HelloRepository
        single { MyRepository() }

        // MyViewModel ViewModel
        viewModel { MainViewModel(get()) }
    }
}