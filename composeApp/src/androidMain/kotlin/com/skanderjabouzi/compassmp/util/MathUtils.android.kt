package com.skanderjabouzi.compassmp.util

import android.hardware.GeomagneticField
import android.hardware.SensorManager
import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.model.RotationVector

private const val AZIMUTH = 0
private const val AXIS_SIZE = 3
private const val ROTATION_MATRIX_SIZE = 9

actual object MathUtils {

    @JvmStatic
    actual fun calculateAzimuth(rotationVector: RotationVector, displayRotation: DisplayRotation): Azimuth {
        val rotationMatrix = getRotationMatrix(rotationVector)
        val remappedRotationMatrix = remapRotationMatrix(rotationMatrix, displayRotation)
        val orientationInRadians = SensorManager.getOrientation(remappedRotationMatrix, FloatArray(AXIS_SIZE))
        val azimuthInRadians = orientationInRadians[AZIMUTH]
        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
        return Azimuth(azimuthInDegrees)
    }

    actual fun getRotationMatrix(rotationVector: RotationVector): FloatArray {
        val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector.toArray())
        return rotationMatrix
    }

    actual fun remapRotationMatrix(rotationMatrix: FloatArray, displayRotation: DisplayRotation): FloatArray {
        return when (displayRotation) {
            DisplayRotation.ROTATION_0 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y)
            DisplayRotation.ROTATION_90 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X)
            DisplayRotation.ROTATION_180 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y)
            DisplayRotation.ROTATION_270 -> remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X)
        }
    }

    actual fun remapRotationMatrix(rotationMatrix: FloatArray, newX: Int, newY: Int): FloatArray {
        val remappedRotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
        SensorManager.remapCoordinateSystem(rotationMatrix, newX, newY, remappedRotationMatrix)
        return remappedRotationMatrix
    }

    actual fun getMagneticDeclination(location: Location): Float {
        val latitude = location.latitude.toFloat()
        val longitude = location.longitude.toFloat()
        val altitude = location.altitude.toFloat()
        val time = location.time
        val geomagneticField = GeomagneticField(latitude , longitude, altitude, time)
        return geomagneticField.declination
    }
}
