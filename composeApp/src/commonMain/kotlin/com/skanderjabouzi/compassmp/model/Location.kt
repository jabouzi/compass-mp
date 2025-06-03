package com.skanderjabouzi.compassmp.model

import kotlin.math.PI

data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val time: Long // UTC time of this fix, in milliseconds since January 1, 1970
)

fun Double.toRadians(): Double {
    return this * PI / 180.0
}