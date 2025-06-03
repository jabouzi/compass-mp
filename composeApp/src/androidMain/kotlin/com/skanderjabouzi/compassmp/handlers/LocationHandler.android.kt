package com.skanderjabouzi.compassmp.handlers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.skanderjabouzi.compassmp.model.LocationStatus
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.util.application // Assuming this provides Application context

actual class LocationHandler actual constructor(
    private val onLocationChanged: (Location) -> Unit,
    private val onLocationStatusChanged: (LocationStatus) -> Unit
) {
    private var locationManager: LocationManager? = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var internalLocationListener: LocationListener? = null

    private fun android.location.Location.toCommonLocation(): Location {
        return Location(
            latitude = this.latitude,
            longitude = this.longitude,
            altitude = if (this.hasAltitude()) this.altitude else 0.0,
            accuracy = if (this.hasAccuracy()) this.accuracy else 0F,
            speed = if (this.hasSpeed()) this.speed else 0F,
            time = this.time
        )
    }

    @SuppressLint("MissingPermission")
    actual fun startUpdates() {
        if (locationManager == null) {
            locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        if (ActivityCompat.checkSelfPermission(
                application,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                application,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
            return
        }

        onLocationStatusChanged(LocationStatus.LOADING)

        if (internalLocationListener == null) {
            internalLocationListener = object : LocationListener {
                override fun onLocationChanged(newLocation: android.location.Location) { // Still android.location.Location here
                    onLocationChanged(newLocation.toCommonLocation()) // Convert and call
                    onLocationStatusChanged(LocationStatus.PRESENT)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String) {
                    onLocationStatusChanged(LocationStatus.LOADING)
                }

                override fun onProviderDisabled(provider: String) {
                    //onLocationChanged(null)
                    onLocationStatusChanged(LocationStatus.NOT_PRESENT)
                }
            }
        }

        try {
            val lastKnownLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation.toCommonLocation()) // Convert and call
                onLocationStatusChanged(LocationStatus.PRESENT)
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // 5 seconds
                10f,   // 10 meters
                internalLocationListener!!
            )
        } catch (e: SecurityException) {
            onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
        } catch (e: Exception) {
            //onLocationChanged(null)
            onLocationStatusChanged(LocationStatus.NOT_PRESENT)
        }
    }

    @SuppressLint("MissingPermission")
    actual fun stopUpdates() {
        internalLocationListener?.let {
            locationManager?.removeUpdates(it)
        }
    }
}