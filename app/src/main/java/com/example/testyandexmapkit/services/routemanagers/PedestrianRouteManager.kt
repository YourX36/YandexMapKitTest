package com.example.testyandexmapkit.services.routemanagers

import android.util.Log
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error


class PedestrianRouteManager(
    private val pedestrianRouter: PedestrianRouter,
) {
    fun buildPedestrianRoute(
        points: List<Point>,
        onSuccess: (List<Route>) -> Unit,
        onError: () -> Unit,
    ) {
        Log.e("PEDESTRIAN", "НАЧАЛО ПОСТРОЕНИЯ МАРШРУТА")
        points.forEach {
            Log.e("PEDESTRIAN", "ТОЧКА: ${it.latitude}")
        }

        val pedestrianPoints = points.map { point ->
            RequestPoint(point, RequestPointType.WAYPOINT, null, null)
        }

        val routeOptions = RouteOptions()
        val timeOptions = TimeOptions()

        pedestrianRouter.requestRoutes(
            pedestrianPoints,
            timeOptions,
            routeOptions,
            object : Session.RouteListener {
                override fun onMasstransitRoutes(p0: MutableList<Route>) {
                    Log.e("PEDESTRIAN", "ПОЛУЧЕННЫЕ ТОЧКИ: $p0")
                    onSuccess(p0)
                }

                override fun onMasstransitRoutesError(p0: Error) {
                    onError()
                }

            }
        )
    }
}