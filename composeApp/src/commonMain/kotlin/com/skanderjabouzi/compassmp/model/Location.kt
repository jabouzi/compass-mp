package com.skanderjabouzi.compassmp.model

import kotlin.math.PI

data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val time: Long? = null // UTC time of this fix, in milliseconds since January 1, 1970
)

fun Double.toRadians(): Double {
    return this * PI / 180.0
}