package com.example.testyandexmapkit.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.testyandexmapkit.di.appModule
import com.example.testyandexmapkit.di.dataModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("695eec89-a484-4693-a640-6b671fc4b6b6")
        MapKitFactory.initialize(this)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, dataModule))
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}