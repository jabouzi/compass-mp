package com.skanderjabouzi.compassmp.handlers

import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.Location
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class LocationHandler actual constructor(
    private val onLocationChanged: (Location) -> Unit,
    private val onLocationStatusChanged: (LocationStatus) -> Unit
) {
    private val locationManager = CLLocationManager()
    private var isStarted = false

    @OptIn(ExperimentalForeignApi::class)
    private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            (didUpdateLocations.firstOrNull() as? CLLocation)?.let { clLocation ->
                clLocation.coordinate.useContents { // Safely access latitude and longitude
                    val commonLocation = Location(
                        latitude = latitude,
                        longitude = longitude,
                        altitude = clLocation.altitude,
                        time = clLocation.timestamp.timeIntervalSince1970().toLong() * 1000
                    )
                    onLocationChanged(commonLocation)
                }
                onLocationStatusChanged(LocationStatus.PRESENT)
            }
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            onLocationStatusChanged(LocationStatus.NOT_PRESENT) // Or a more specific error status
        }

        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            checkPermissionsAndNotifyStatus()
        }
    }

    init {
        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest // Or other as needed
    }

    private fun checkPermissionsAndNotifyStatus() {
        when (CLLocationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                onLocationStatusChanged(LocationStatus.LOADING) // Or PRESENT if already have a location
            }
            kCLAuthorizationStatusDenied -> {
                onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
            }
            kCLAuthorizationStatusNotDetermined -> {
                onLocationStatusChanged(LocationStatus.NOT_PRESENT) // Or a specific "needs permission" state
                // Consider requesting permission here or providing a method to do so
                locationManager.requestWhenInUseAuthorization()
            }
            else -> {
                onLocationStatusChanged(LocationStatus.NOT_PRESENT)
            }
        }
    }

    actual fun startUpdates() {
        checkPermissionsAndNotifyStatus()
        val authStatus = CLLocationManager.authorizationStatus()
        if (authStatus == kCLAuthorizationStatusAuthorizedWhenInUse || authStatus == kCLAuthorizationStatusAuthorizedAlways) {
            if (CLLocationManager.locationServicesEnabled()) {
                locationManager.startUpdatingLocation()
                isStarted = true
                onLocationStatusChanged(LocationStatus.LOADING)
            } else {
                onLocationStatusChanged(LocationStatus.NOT_PRESENT) // Services disabled
            }
        } else if (authStatus == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization() // Request if not yet determined
        }
        // If denied, status is already set by checkPermissionsAndNotifyStatus
    }

    actual fun stopUpdates() {
        if (isStarted) {
            locationManager.stopUpdatingLocation()
            isStarted = false
            // Do not change status to NOT_PRESENT here if it's just stopping,
            // unless explicitly required by logic (e.g. true north turned off)
        }
    }
}