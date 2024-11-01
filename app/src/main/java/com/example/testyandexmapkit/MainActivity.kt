package com.example.testyandexmapkit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testyandexmapkit.services.LocationService
import com.example.testyandexmapkit.ui.screens.MapScreen
import com.example.testyandexmapkit.ui.theme.TestYandexMapKitTheme
import com.yandex.mapkit.MapKitFactory
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private lateinit var locationServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationServiceIntent = Intent(this, LocationService::class.java)
        startService(locationServiceIntent)
        setContent {
            TestYandexMapKitTheme {
                MapScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        stopService(locationServiceIntent)
        super.onDestroy()
    }
}