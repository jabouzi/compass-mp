package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth

actual fun handleHapticFeedback(
    azimuth: Azimuth,
    lastHapticFeedbackPoint: Azimuth?,
    onUpdateLastPoint: (Azimuth) -> Unit
) {
}