package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.model.RotationVector

actual object MathUtils {
    actual fun calculateAzimuth(
        rotationVector: RotationVector,
        displayRotation: DisplayRotation
    ): Azimuth {
        TODO("Not yet implemented")
    }

    actual fun getRotationMatrix(rotationVector: RotationVector): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        displayRotation: DisplayRotation
    ): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        newX: Int,
        newY: Int
    ): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getMagneticDeclination(location: Location): Float {
        TODO("Not yet implemented")
    }

}