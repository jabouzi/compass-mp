package com.skanderjabouzi.compassmp.screens

import androidx.compose.runtime.*
import com.skanderjabouzi.compassmp.handlers.LocationHandler
import com.skanderjabouzi.compassmp.handlers.SensorHandler
import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.viewmodel.CompassViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen() {
    val compassViewModel: CompassViewModel = koinInject()

    val sensorHandler = remember {
        SensorHandler(
            onAzimuthChanged = { newAzimuth -> compassViewModel.updateAzimuth(newAzimuth) },
            onSensorAccuracyChanged = { newAccuracy -> compassViewModel.updateSensorAccuracy(newAccuracy) },
            isTrueNorthEnabled = { compassViewModel.trueNorth.value },
            getCurrentLocation = { compassViewModel.location.value },
            getCurrentLocationStatus = { compassViewModel.locationStatus.value }
        )
    }

    val locationHandler = remember {
        LocationHandler(
            // appContext is removed; actual LocationHandler implementations manage context if needed.
            onLocationChanged = { newLocation -> // newLocation is your common Location type
                compassViewModel.updateLocation(newLocation, compassViewModel.locationStatus.value)
            },
            onLocationStatusChanged = { newStatus ->
                compassViewModel.updateLocation(compassViewModel.location.value, newStatus)
            }
        )
    }

    val trueNorthEnabled by compassViewModel.trueNorth.collectAsState()
    val hapticFeedbackEnabled by compassViewModel.hapticFeedback.collectAsState()
    var screenOrientationLocked by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        sensorHandler.start()
        // Location updates are managed by trueNorthEnabled state
        onDispose {
            sensorHandler.stop()
            locationHandler.stopUpdates() // Ensure location updates are stopped
        }
    }

    LaunchedEffect(trueNorthEnabled) {
        if (trueNorthEnabled) {
            locationHandler.startUpdates()
        } else {
            locationHandler.stopUpdates()
            // Update ViewModel state when true north is disabled and location updates stop
            compassViewModel.updateLocation(null, LocationStatus.NOT_PRESENT)
        }
    }

    // Assuming CompassScreen is a common composable
    CompassScreen(
        compassViewModel = compassViewModel,
        trueNorth = trueNorthEnabled,
        hapticFeedback = hapticFeedbackEnabled,
        screenOrientationLocked = screenOrientationLocked,
        onTrueNorthChanged = { enabled -> compassViewModel.setTrueNorth(enabled) },
        onHapticFeedbackChanged = { enabled -> compassViewModel.setHapticFeedback(enabled) },
        onScreenOrientationChanged = { screenOrientationLocked = it }
    )
}