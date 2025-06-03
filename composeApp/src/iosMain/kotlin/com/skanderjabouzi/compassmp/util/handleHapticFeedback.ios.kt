package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual fun handleHapticFeedback(
    azimuth: Azimuth,
    lastHapticFeedbackPoint: Azimuth?,
    onUpdateLastPoint: (Azimuth) -> Unit
) {
    val hapticFeedbackInterval = 2.0f

    lastHapticFeedbackPoint?.let { lastPoint ->
        val boundaryStart = lastPoint - hapticFeedbackInterval
        val boundaryEnd = lastPoint + hapticFeedbackInterval

        if (!isAzimuthBetweenTwoPoints(azimuth, boundaryStart, boundaryEnd)) {
            val closestIntervalPoint = getClosestNumberFromInterval(azimuth.degrees, hapticFeedbackInterval)
            onUpdateLastPoint(Azimuth(closestIntervalPoint))

            // Trigger haptic feedback
            val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
            generator.prepare() // Prepare the generator for lower latency
            generator.impactOccurred()
        }
    } ?: run {
        val closestIntervalPoint = getClosestNumberFromInterval(azimuth.degrees, hapticFeedbackInterval)
        onUpdateLastPoint(Azimuth(closestIntervalPoint))
        // If haptic feedback should also occur for the first point:
        // val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        // generator.prepare()
        // generator.impactOccurred()
    }
}