package com.skanderjabouzi.compassmp.handlers

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.SensorAccuracy
import com.skanderjabouzi.compassmp.model.RotationVector
import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.util.MathUtils.calculateAzimuth
import com.skanderjabouzi.compassmp.util.MathUtils.getMagneticDeclination
import com.skanderjabouzi.compassmp.util.application

actual class SensorHandler actual constructor(
    private val onAzimuthChanged: (Azimuth) -> Unit,
    private val onSensorAccuracyChanged: (SensorAccuracy) -> Unit,
    private val isTrueNorthEnabled: () -> Boolean,
    private val getCurrentLocation: () -> Location?,
    private val getCurrentLocationStatus: () -> LocationStatus,
) {
    private var sensorManager: SensorManager? = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorEventListener: CompassSensorEventListener = CompassSensorEventListener()

    actual fun start() {
        sensorManager?.let { manager ->
            manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let { sensor ->
                manager.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
            manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let { sensor ->
                manager.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    actual fun stop() {
        sensorManager?.unregisterListener(sensorEventListener)
    }

    private inner class CompassSensorEventListener : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            when (sensor?.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    val newAccuracy = when (accuracy) {
                        SensorManager.SENSOR_STATUS_NO_CONTACT -> SensorAccuracy.NO_CONTACT
                        SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                        else -> SensorAccuracy.NO_CONTACT
                    }
                    onSensorAccuracyChanged(newAccuracy)
                }
            }
        }

        @SuppressLint("NewApi") // Keep if other NewApi usages exist, or remove if not needed.
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        val rotationVector = RotationVector(it.values[0], it.values[1], it.values[2])

                        // Get current display rotation
                        val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                        // Use windowManager.defaultDisplay.rotation consistently.
                        // application.display can throw UnsupportedOperationException on non-visual contexts (API 30+).
                        @Suppress("DEPRECATION")
                        val androidRotation = windowManager.defaultDisplay.rotation

                        val currentDisplayRotation = when (androidRotation) {
                            Surface.ROTATION_0 -> com.skanderjabouzi.compassmp.model.DisplayRotation.ROTATION_0
                            Surface.ROTATION_90 -> com.skanderjabouzi.compassmp.model.DisplayRotation.ROTATION_90
                            Surface.ROTATION_180 -> com.skanderjabouzi.compassmp.model.DisplayRotation.ROTATION_180
                            Surface.ROTATION_270 -> com.skanderjabouzi.compassmp.model.DisplayRotation.ROTATION_270
                            else -> com.skanderjabouzi.compassmp.model.DisplayRotation.ROTATION_0 // Default
                        }

                        val magneticAzimuth = calculateAzimuth(rotationVector, currentDisplayRotation)

                        val finalAzimuth = if (isTrueNorthEnabled()) {
                            val currentLoc = getCurrentLocation()
                            if (currentLoc != null && getCurrentLocationStatus() == LocationStatus.PRESENT) {
                                val declination = getMagneticDeclination(currentLoc)
                                magneticAzimuth.plus(declination)
                            } else {
                                magneticAzimuth
                            }
                        } else {
                            magneticAzimuth
                        }
                        onAzimuthChanged(finalAzimuth)
                    }
                }
            }
        }
    }
}