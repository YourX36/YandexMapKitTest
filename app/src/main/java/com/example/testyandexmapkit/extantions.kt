package com.example.testyandexmapkit

import com.example.testyandexmapkit.model.LatLng
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location

fun Location.mapToLatLng() : LatLng {
    return LatLng(
        this.position.latitude,
        this.position.longitude
    )
}

fun Location.mapToYandexPoint() : Point {
    return Point(
        position.latitude,
        position.longitude
    )
}

fun List<LatLng>.mapToYandexPoint() : List<Point> {
    return this.map {
        Point(it.latitude, it.longitude)
    }
}
fun LatLng.mapToYandexPoint() : Point {
    return Point(
        latitude,
        longitude
    )
}

fun List<LatLng>.invoke() : List<RequestPoint> {
    return this.map { point ->
        RequestPoint(point.mapToYandexPoint(), RequestPointType.WAYPOINT, null, null)
    }
}