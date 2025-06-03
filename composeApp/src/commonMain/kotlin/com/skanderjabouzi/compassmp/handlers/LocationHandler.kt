package com.skanderjabouzi.compassmp.handlers

import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.Location

expect class LocationHandler(
    onLocationChanged: (Location) -> Unit,
    onLocationStatusChanged: (LocationStatus) -> Unit
) {
    fun startUpdates()
    fun stopUpdates()
    // fun requestLocationPermissions() // You might need a way to trigger permission requests
}