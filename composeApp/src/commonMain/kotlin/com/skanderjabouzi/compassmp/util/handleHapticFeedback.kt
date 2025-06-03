package com.skanderjabouzi.compassmp.util

import com.skanderjabouzi.compassmp.model.Azimuth

expect fun handleHapticFeedback(
    azimuth: Azimuth,
    lastHapticFeedbackPoint: Azimuth?,
    onUpdateLastPoint: (Azimuth) -> Unit
)