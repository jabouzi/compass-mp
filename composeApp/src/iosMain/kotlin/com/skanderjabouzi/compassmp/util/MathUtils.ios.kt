package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth
import com.skanderjabouzi.compassmp.model.DisplayRotation
import com.skanderjabouzi.compassmp.model.Location
import com.skanderjabouzi.compassmp.model.RotationVector
import kotlin.math.PI
// import kotlin.math.asin // Not used in the provided snippet
import kotlin.math.atan2
// import kotlin.math.cos // Not used in the provided snippet
import kotlin.math.pow
// import kotlin.math.sin // Not used in the provided snippet
import kotlin.math.sqrt
import kotlin.math.roundToInt // Was in previous MathUtils, ensure it's there if needed by other functions

// Constants for remapRotationMatrix, mimicking Android's SensorManager.AXIS_*
private const val IOS_AXIS_X = 1
private const val IOS_AXIS_Y = 2
private const val IOS_AXIS_Z = 3
private const val IOS_AXIS_MINUS_X = 129
private const val IOS_AXIS_MINUS_Y = 130
private const val IOS_AXIS_MINUS_Z = 131

private const val ROTATION_MATRIX_SIZE = 9

actual object MathUtils {

    actual fun calculateAzimuth(
        rotationVector: RotationVector,
        displayRotation: DisplayRotation
    ): Azimuth {
        val rotationMatrix = getRotationMatrix(rotationVector)
        val remappedRotationMatrix = remapRotationMatrix(rotationMatrix, displayRotation)

        val azimuthInRadians = atan2(remappedRotationMatrix[1], remappedRotationMatrix[4])
        var azimuthInDegrees = (azimuthInRadians.toDouble() * 180.0 / PI).toFloat()

        if (azimuthInDegrees < 0) {
            azimuthInDegrees += 360f
        }
        return Azimuth(azimuthInDegrees)
    }

    actual fun getRotationMatrix(rotationVector: RotationVector): FloatArray {
        val q = FloatArray(4)
        q[0] = rotationVector.x
        q[1] = rotationVector.y
        q[2] = rotationVector.z

        // Calculate the w component of the quaternion (q[3])
        // Assumes x, y, z are rotationVector.x, .y, .z and q[0], q[1], q[2] respectively,
        // and that (q[0], q[1], q[2], q[3]) form a unit quaternion.
        // q[3] = sqrt(1.0f - (q[0]^2 + q[1]^2 + q[2]^2))
        val t = 1.0f - (q[0].pow(2) + q[1].pow(2) + q[2].pow(2))
        q[3] = if (t < 0.0f) 0.0f else sqrt(t)

        val R = FloatArray(ROTATION_MATRIX_SIZE)

        val qx = q[0]
        val qy = q[1]
        val qz = q[2]
        val qw = q[3]

        R[0] = 1f - 2f * qy * qy - 2f * qz * qz
        R[1] = 2f * qx * qy - 2f * qz * qw
        R[2] = 2f * qx * qz + 2f * qy * qw

        R[3] = 2f * qx * qy + 2f * qz * qw
        R[4] = 1f - 2f * qx * qx - 2f * qz * qz
        R[5] = 2f * qy * qz - 2f * qx * qw

        R[6] = 2f * qx * qz - 2f * qy * qw
        R[7] = 2f * qy * qz + 2f * qx * qw
        R[8] = 1f - 2f * qx * qx - 2f * qy * qy

        return R
    }

    actual fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        displayRotation: DisplayRotation
    ): FloatArray {
        return when (displayRotation) {
            DisplayRotation.ROTATION_0 -> remapRotationMatrix(rotationMatrix, IOS_AXIS_X, IOS_AXIS_Y)
            DisplayRotation.ROTATION_90 -> remapRotationMatrix(rotationMatrix, IOS_AXIS_Y, IOS_AXIS_MINUS_X)
            DisplayRotation.ROTATION_180 -> remapRotationMatrix(rotationMatrix, IOS_AXIS_MINUS_X, IOS_AXIS_MINUS_Y)
            DisplayRotation.ROTATION_270 -> remapRotationMatrix(rotationMatrix, IOS_AXIS_MINUS_Y, IOS_AXIS_X)
        }
    }

    actual fun remapRotationMatrix(
        rotationMatrix: FloatArray,
        newX: Int,
        newY: Int
    ): FloatArray {
        val outR = FloatArray(ROTATION_MATRIX_SIZE)

        fun getRow(matrix: FloatArray, rowIndex: Int): FloatArray {
            val start = rowIndex * 3
            return floatArrayOf(matrix[start], matrix[start + 1], matrix[start + 2])
        }

        fun resolveAxis(axisCode: Int): Pair<Int, Float> {
            val axis = axisCode and 0x7F
            val sign = if ((axisCode and 0x80) != 0) -1.0f else 1.0f
            return Pair(axis - 1, sign)
        }

        val (xSrcAxisIdx, xSign) = resolveAxis(newX)
        val (ySrcAxisIdx, ySign) = resolveAxis(newY)

        val xAxis = getRow(rotationMatrix, xSrcAxisIdx).map { it * xSign }.toFloatArray()
        val yAxis = getRow(rotationMatrix, ySrcAxisIdx).map { it * ySign }.toFloatArray()

        val zAxis = FloatArray(3)
        zAxis[0] = xAxis[1] * yAxis[2] - xAxis[2] * yAxis[1]
        zAxis[1] = xAxis[2] * yAxis[0] - xAxis[0] * yAxis[2]
        zAxis[2] = xAxis[0] * yAxis[1] - xAxis[1] * yAxis[0]

        val normZ = sqrt(zAxis[0].pow(2) + zAxis[1].pow(2) + zAxis[2].pow(2))
        if (normZ > 1e-6f) {
            zAxis[0] /= normZ
            zAxis[1] /= normZ
            zAxis[2] /= normZ
        }

        outR[0] = xAxis[0]; outR[1] = xAxis[1]; outR[2] = xAxis[2]
        outR[3] = yAxis[0]; outR[4] = yAxis[1]; outR[5] = yAxis[2]
        outR[6] = zAxis[0]; outR[7] = zAxis[1]; outR[8] = zAxis[2]

        return outR
    }

    actual fun getMagneticDeclination(location: Location): Float {
        println("Warning: MathUtils.getMagneticDeclination not implemented for iOS. Returning 0f.")
        return 0.0f
    }
}