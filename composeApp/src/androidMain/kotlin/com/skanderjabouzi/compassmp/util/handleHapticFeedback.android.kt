package com.skanderjabouzi.compassmp.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.skanderjabouzi.compassmp.model.Azimuth

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

            // Trigger haptic feedback using Vibrator service
            val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For API 29+ (Android 10+), EFFECT_CLICK is a good predefined effect
                    // similar to VIRTUAL_KEY.
                    val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    it.vibrate(effect)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For API 26-28 (Android 8.0-9.0), create a short one-shot vibration.
                    // 20ms is a common duration for a light tap.
                    @Suppress("DEPRECATION")
                    val effect = VibrationEffect.createOneShot(20L, VibrationEffect.DEFAULT_AMPLITUDE)
                    it.vibrate(effect)
                } else {
                    // For API < 26 (before Android 8.0), use the deprecated vibrate method.
                    @Suppress("DEPRECATION")
                    it.vibrate(20L) // 20ms duration
                }
            }
        }
    } ?: run {
        val closestIntervalPoint = getClosestNumberFromInterval(azimuth.degrees, hapticFeedbackInterval)
        onUpdateLastPoint(Azimuth(closestIntervalPoint))
        // If haptic feedback should also occur for the first point,
        // the vibration logic above would need to be duplicated here.
        // Currently, it only vibrates if lastHapticFeedbackPoint was not null.
    }
}