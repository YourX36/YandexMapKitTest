package com.example.testyandexmapkit.ui.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.testyandexmapkit.R
import com.example.testyandexmapkit.invoke
import com.example.testyandexmapkit.mapToYandexPoint
import com.example.testyandexmapkit.model.LatLng
import com.example.testyandexmapkit.services.routemanagers.PedestrianRouteManager
import com.example.testyandexmapkit.services.routemanagers.RouteManager
import com.example.testyandexmapkit.utils.LocationEvent
import com.example.testyandexmapkit.utils.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    mapView: MapView
) {

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val locationViewModel: LocationViewModel = koinViewModel()
    val context = LocalContext.current

    val drivingRouter =
        remember { DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE) }
    val routeManager = remember { RouteManager(drivingRouter) }

    val pedestrianRouter = remember {
        val router = TransportFactory.getInstance().createPedestrianRouter()
        Log.e("PEDESTRIAN", "Инициализация PedestrianRouter: $router")
        router
    }

    val locationViewState by locationViewModel.viewState.collectAsState()

    val routePoints: List<LatLng> by remember {
        derivedStateOf { locationViewState.routePoints }
    }

    val userLocation by remember {
        derivedStateOf { locationViewState.location }
    }

    var userLocationLayer by remember { mutableStateOf<MapObjectCollection?>(null) }
    var routeLayer by remember { mutableStateOf<MapObjectCollection?>(null) }

    val listener = object : Session.RouteListener {
        override fun onMasstransitRoutes(routes: MutableList<Route>) {
            if (routes.isNotEmpty()) {
                routeLayer?.clear()
                routes.forEach {
                    routeLayer?.addPolyline(it.geometry)
                }
            }
        }
        override fun onMasstransitRoutesError(p0: Error) {
            Log.e("MapScreen", "Ошибка построения маршрута: ${p0}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(factory = { context ->
            mapView.apply {
                mapWindow.map.isNightModeEnabled = true

                userLocationLayer = this.mapWindow.map.mapObjects.addCollection()
                routeLayer = this.mapWindow.map.mapObjects.addCollection()
            }
        }, update = { map ->
            userLocation?.let { location ->
                if (userLocationLayer != null) {
                    updateLocation(
                        location = location,
                        locationLayer = userLocationLayer!!,
                        patrolImage = R.drawable.ic_police_point,
                        context = context
                    )
                }
            }
        }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .width(220.dp)
                .height(60.dp)
                .align(Alignment.BottomEnd)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxSize(),
                onClick = {
                    locationViewModel.obtainEvent(LocationEvent.LoadRoutePoints)
                },
            ) {
                Text(text = "Получить маршрут")
            }
        }
    }
    LaunchedEffect(routePoints, userLocation) {
        userLocation?.let {
            val points: List<Point> = when {

                routePoints.isNotEmpty() -> {
                    val yandexPoint = routePoints + routePoints.first()
                    val polylinePoints =
                        listOf(userLocation!!.mapToYandexPoint()) + yandexPoint.mapToYandexPoint()
                    polylinePoints
                }

                else -> {
                    emptyList()
                }
            }

            if (points.isNotEmpty()) {
                pedestrianRouter.requestRoutes(
                    routePoints.invoke(),
                    TimeOptions(),
                    RouteOptions(),
                    listener
                )
/*                routeManager.buildRoute(
                    points = points,
                    onSuccess = { routes ->
                        if (routes.isNotEmpty()) {
                            routeLayer?.clear()
                            routes.forEach {
                                routeLayer?.addPolyline(it.geometry)
                            }
                        }
                    },
                    onError = { error ->
                        Log.e("MapScreen", "Ошибка построения маршрута: ${error}")
                    }
                )*/
            } else {
                routeLayer?.clear()
            }
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if(locationPermissionState.status.isGranted) {
            locationViewModel.obtainEvent(LocationEvent.StartTracking)
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }
    DisposableEffect(Unit) {
        onDispose {

        }
    }
}
private fun updateLocation(
    location: LatLng,
    locationLayer: MapObjectCollection,
    patrolImage: Int,
    context: Context,
) {
    locationLayer.clear()
    locationLayer.addPlacemark {
        it.geometry = Point(location.latitude, location.longitude)
        it.setIcon(ImageProvider.fromResource(context, patrolImage))
    }
}
