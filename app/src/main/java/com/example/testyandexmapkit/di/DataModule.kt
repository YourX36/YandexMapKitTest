package com.example.testyandexmapkit.di

import com.example.testyandexmapkit.repository.LocationRepository
import org.koin.dsl.module

val dataModule = module {

    single<LocationRepository> {
        LocationRepository()
    }
}