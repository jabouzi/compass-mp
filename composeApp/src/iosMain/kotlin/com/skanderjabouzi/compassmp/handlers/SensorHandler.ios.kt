package com.skanderjabouzi.compassmp.handlers

import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.SensorAccuracy
import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.Location
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLHeadingFilterNone
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.UIKit.UIDevice
import platform.darwin.NSObject

actual class SensorHandler actual constructor(
    private val onAzimuthChanged: (Azimuth) -> Unit,
    private val onSensorAccuracyChanged: (SensorAccuracy) -> Unit,
    private val isTrueNorthEnabled: () -> Boolean,
    private val getCurrentLocation: () -> Location?, // Used for declination if trueNorth is manual
    private val getCurrentLocationStatus: () -> LocationStatus
) {
    private val locationManager = CLLocationManager()
    private var isStarted = false

    private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
            if (!isStarted) return

            val headingToUse = if (isTrueNorthEnabled() && didUpdateHeading.trueHeading >= 0) {
                didUpdateHeading.trueHeading // trueHeading is already declination-adjusted
            } else {
                didUpdateHeading.magneticHeading
            }

            if (headingToUse < 0) { // Invalid heading
                onSensorAccuracyChanged(SensorAccuracy.NO_CONTACT) // Or UNRELIABLE
                return
            }

            onAzimuthChanged(Azimuth(headingToUse.toFloat()))

            // Map headingAccuracy (degrees) to SensorAccuracy enum
            val accuracy = didUpdateHeading.headingAccuracy
            val sensorAccuracy = when {
                accuracy < 0 -> SensorAccuracy.UNRELIABLE // Invalid
                accuracy <= 5 -> SensorAccuracy.HIGH
                accuracy <= 15 -> SensorAccuracy.MEDIUM
                accuracy <= 30 -> SensorAccuracy.LOW
                else -> SensorAccuracy.UNRELIABLE
            }
            onSensorAccuracyChanged(sensorAccuracy)
        }

        override fun locationManagerShouldDisplayHeadingCalibration(manager: CLLocationManager): Boolean {
            return true // Display calibration UI if needed
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            // Handle heading errors, e.g., interference
            if (manager.headingAvailable()) { // Check if it's specifically a heading error
                onSensorAccuracyChanged(SensorAccuracy.NO_CONTACT) // Or some error state
            }
        }
    }

    init {
        locationManager.delegate = delegate
        locationManager.headingFilter = kCLHeadingFilterNone // Report all changes
        // Desired accuracy for location services if SensorHandler also starts them for true heading.
        // If LocationHandler is solely responsible, this might not be needed here.
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }

    actual fun start() {
        if (!CLLocationManager.headingAvailable()) {
            onSensorAccuracyChanged(SensorAccuracy.NO_CONTACT) // Or a specific "not available" state
            return
        }
        // Set heading orientation based on current UI orientation
        // This is important for the heading to be correctly aligned with the device's screen.
        // Note: Getting UIInterfaceOrientation might need to be done on the main thread
        // and might require access to the current UIWindowScene.
        // For simplicity, this example uses UIDeviceOrientation, but UIInterfaceOrientation is preferred.
        locationManager.headingOrientation = mapDeviceOrientationToCLInterfaceOrientation(UIDevice.currentDevice.orientation)

        locationManager.startUpdatingHeading()
        isStarted = true
        // If trueNorth requires location, ensure location updates are also running (e.g., via LocationHandler)
    }

    actual fun stop() {
        if (isStarted) {
            locationManager.stopUpdatingHeading()
            isStarted = false
        }
    }

    // Helper to map UIDeviceOrientation to CLDeviceOrientation (used for headingOrientation)
    // A more robust solution would use UIWindowScene.interfaceOrientation
    private fun mapDeviceOrientationToCLInterfaceOrientation(deviceOrientation: platform.UIKit.UIDeviceOrientation): platform.CoreLocation.CLDeviceOrientation {
        return when (deviceOrientation) {
            platform.UIKit.UIDeviceOrientation.UIDeviceOrientationPortrait -> platform.CoreLocation.CLDeviceOrientationPortrait
            platform.UIKit.UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> platform.CoreLocation.CLDeviceOrientationPortraitUpsideDown
            platform.UIKit.UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> platform.CoreLocation.CLDeviceOrientationLandscapeLeft
            platform.UIKit.UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> platform.CoreLocation.CLDeviceOrientationLandscapeRight
            else -> platform.CoreLocation.CLDeviceOrientationUnknown    // Or current interface orientation
        }
    }
}