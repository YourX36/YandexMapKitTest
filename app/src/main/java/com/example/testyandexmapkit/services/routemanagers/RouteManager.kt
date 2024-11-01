package com.example.testyandexmapkit.services.routemanagers

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.Error

class RouteManager(private val drivingRouter: DrivingRouter) {

    // Функция для построения маршрута на основе списка точек
    fun buildRoute(
        points: List<Point>,
        onSuccess: (List<DrivingRoute>) -> Unit,
        onError: (Error) -> Unit
    ) {
        if (points.size < 2) {
            throw IllegalArgumentException("Нужно минимум две точки для построения маршрута")
        }
        val drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }
        val drivingPoints = points.map { point ->
            RequestPoint(point, RequestPointType.WAYPOINT ,null, null)
        }

        // Запрос маршрута
        drivingRouter.requestRoutes(
            drivingPoints,
            drivingOptions,
            VehicleOptions(),
            object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                    onSuccess(routes)
                }

                override fun onDrivingRoutesError(p0: Error) {
                    onError(p0)
                }
            }
        )
    }
}