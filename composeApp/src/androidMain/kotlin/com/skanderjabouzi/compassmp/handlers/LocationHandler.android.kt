package com.skanderjabouzi.compassmp.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private var activityHolder: ComponentActivity? = null
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

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

    /**
     * Call this from your Android Activity's onCreate (or an equivalent lifecycle point)
     * to enable LocationHandler to request permissions.
     */
    fun attachActivity(activity: ComponentActivity) {
        this.activityHolder = activity
        // The ActivityResultLauncher must be registered before the Activity is in a CREATED state.
        // Typically, this is done as an instance field initializer or in onCreate.
        this.locationPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                onLocationStatusChanged(LocationStatus.LOADING)
                startLocationUpdatesInternal() // Proceed with starting updates
            } else {
                onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
            }
        }
    }

    /**
     * Call this from your Android Activity's onDestroy (or an equivalent lifecycle point)
     * to clear the Activity reference and prevent potential memory leaks.
     */
    fun detachActivity() {
        this.activityHolder = null
        // Note: locationPermissionLauncher does not need explicit unregistering here
        // as it's tied to the lifecycle of the ComponentActivity it was registered with.
    }

    actual fun startUpdates() {
        val currentActivity = activityHolder

        if (currentActivity == null || !::locationPermissionLauncher.isInitialized) {
            // Activity not attached or launcher not ready: Cannot request permissions.
            // Fallback to checking permissions with application context.
            if (ActivityCompat.checkSelfPermission(
                    application, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    application, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
                // Optionally, log that attachActivity should be called to enable permission requests.
                // e.g., Log.w("LocationHandler", "Activity not attached. Cannot request location permissions.")
                return
            }
            // Permissions are already granted, or we can't request them. Proceed to start updates.
            onLocationStatusChanged(LocationStatus.LOADING)
            startLocationUpdatesInternal()
            return
        }

        // Activity is attached, and launcher is initialized. We can request permissions.
        if (ActivityCompat.checkSelfPermission(
                currentActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions. The actual starting of updates will be handled by the launcher's callback.
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            // Status will be updated by the launcher's callback. Don't start updates here.
            return
        }

        // Permissions are already granted. Proceed with starting updates.
        onLocationStatusChanged(LocationStatus.LOADING)
        startLocationUpdatesInternal()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdatesInternal() {
        if (locationManager == null) {
            locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        if (internalLocationListener == null) {
            internalLocationListener = object : LocationListener {
                override fun onLocationChanged(newLocation: android.location.Location) {
                    onLocationChanged(newLocation.toCommonLocation())
                    onLocationStatusChanged(LocationStatus.PRESENT)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String) {
                    onLocationStatusChanged(LocationStatus.LOADING)
                }

                override fun onProviderDisabled(provider: String) {
                    onLocationStatusChanged(LocationStatus.NOT_PRESENT)
                }
            }
        }

        try {
            val lastKnownLocationGps = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastKnownLocationNetwork = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val lastKnownLocation = lastKnownLocationGps ?: lastKnownLocationNetwork

            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation.toCommonLocation())
                onLocationStatusChanged(LocationStatus.PRESENT)
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // 5 seconds
                10f,   // 10 meters
                internalLocationListener!!
            )
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000L,
                10f,
                internalLocationListener!!
            )
        } catch (e: SecurityException) {
            onLocationStatusChanged(LocationStatus.PERMISSION_DENIED)
        } catch (e: Exception) {
            onLocationStatusChanged(LocationStatus.NOT_PRESENT)
        }
    }

    @SuppressLint("MissingPermission")
    actual fun stopUpdates() {
        internalLocationListener?.let {
            locationManager?.removeUpdates(it)
        }
        // internalLocationListener = null // Optional: clear listener if it should be recreated
    }
}