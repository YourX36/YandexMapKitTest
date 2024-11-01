package com.example.testyandexmapkit.di

import com.example.testyandexmapkit.utils.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<LocationViewModel> {
        LocationViewModel(
            locationRepository = get()
        )
    }
}