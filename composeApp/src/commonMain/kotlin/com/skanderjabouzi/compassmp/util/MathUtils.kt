package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.model.RotationVector
import kotlin.math.roundToInt

expect object MathUtils {
    fun calculateAzimuth(
        rotationVector: RotationVector,
        displayRotation: DisplayRotation
    ): Azimuth

    fun getRotationMatrix(rotationVector: RotationVector): FloatArray
    fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        displayRotation: DisplayRotation
    ): FloatArray

    fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        newX: Int,
        newY: Int
    ): FloatArray

    fun getMagneticDeclination(location: Location): Float
}

fun getClosestNumberFromInterval(number: Float, interval: Float): Float =
    (number / interval).roundToInt() * interval

/**
 * @see <a href="https://math.stackexchange.com/questions/2275439/check-if-point-on-circle-is-between-two-other-points-on-circle">Stackexchange</a>
 */
fun isAzimuthBetweenTwoPoints(azimuth: Azimuth, pointA: Azimuth, pointB: Azimuth): Boolean {
    val aToB = (pointB.degrees - pointA.degrees + 360f) % 360f
    val aToAzimuth = (azimuth.degrees - pointA.degrees + 360f) % 360f
    return aToB <= 180f != aToAzimuth > aToB
}