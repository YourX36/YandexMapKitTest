package com.example.testyandexmapkit.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.testyandexmapkit.mapToLatLng
import com.example.testyandexmapkit.repository.LocationRepository
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.location.Purpose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LocationService(
) : Service() {
    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null
    private lateinit var locationRepository: LocationRepository

    override fun onCreate() {
        super.onCreate()
        Log.e("LOCATION_SERVICE_REPOSITORY" , "Старт сервиса")
        locationManager = MapKitFactory.getInstance().createLocationManager()
        locationRepository = get()

        locationListener = object : LocationListener {
            override fun onLocationUpdated(location: Location) {
                Log.e("LOCATION_SERVICE_REPOSITORY" , "обновление локации: $location ")
                sendLocationToServer(location)
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
                // Обработка статуса
            }
        }

        // Запуск обновлений местоположения
        locationManager.subscribeForLocationUpdates(
            0.0, 5000, 0.0, true, FilteringMode.OFF, Purpose.GENERAL, locationListener!!
        )
    }

    private fun sendLocationToServer(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            locationRepository.updateLocation(location.mapToLatLng())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unsubscribe(locationListener!!)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null  // Возвращаем null, если не нужно связываться с компонентом
    }
}