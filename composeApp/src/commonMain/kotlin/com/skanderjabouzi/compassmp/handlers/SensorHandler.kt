package com.skanderjabouzi.compassmp.handlers

import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.SensorAccuracy
import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.Location


expect class SensorHandler(
    onAzimuthChanged: (Azimuth) -> Unit,
    onSensorAccuracyChanged: (SensorAccuracy) -> Unit,
    isTrueNorthEnabled: () -> Boolean,
    getCurrentLocation: () -> Location?,
    getCurrentLocationStatus: () -> LocationStatus,
) {
    fun start()
    fun stop()
}