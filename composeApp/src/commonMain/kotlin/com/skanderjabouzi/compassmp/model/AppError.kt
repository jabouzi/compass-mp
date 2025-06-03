package com.skanderjabouzi.compassmp.model

import compass_mp.composeapp.generated.resources.Res
import compass_mp.composeapp.generated.resources.location_error_message
import compass_mp.composeapp.generated.resources.sensor_error_message
import org.jetbrains.compose.resources.StringResource

enum class AppError(val messageId: StringResource) {
    SENSOR_MANAGER_NOT_PRESENT(Res.string.sensor_error_message),
    ROTATION_VECTOR_SENSOR_NOT_AVAILABLE(Res.string.sensor_error_message),
    ROTATION_VECTOR_SENSOR_FAILED(Res.string.sensor_error_message),
    MAGNETIC_FIELD_SENSOR_NOT_AVAILABLE(Res.string.sensor_error_message),
    MAGNETIC_FIELD_SENSOR_FAILED(Res.string.sensor_error_message),
    LOCATION_MANAGER_NOT_PRESENT(Res.string.location_error_message),
    LOCATION_DISABLED(Res.string.location_error_message),
    NO_LOCATION_PROVIDER_AVAILABLE(Res.string.location_error_message)
}
