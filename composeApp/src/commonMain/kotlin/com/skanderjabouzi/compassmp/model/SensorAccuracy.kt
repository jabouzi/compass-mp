package com.skanderjabouzi.compassmp.model

import compass_mp.composeapp.generated.resources.Res
import compass_mp.composeapp.generated.resources.ic_sensor_high_signal
import compass_mp.composeapp.generated.resources.ic_sensor_low_signal
import compass_mp.composeapp.generated.resources.ic_sensor_medium_signal
import compass_mp.composeapp.generated.resources.ic_sensor_no_signal
import compass_mp.composeapp.generated.resources.ic_sensor_unreliable_signal
import compass_mp.composeapp.generated.resources.sensor_accuracy_high
import compass_mp.composeapp.generated.resources.sensor_accuracy_low
import compass_mp.composeapp.generated.resources.sensor_accuracy_medium
import compass_mp.composeapp.generated.resources.sensor_accuracy_no_contact
import compass_mp.composeapp.generated.resources.sensor_accuracy_unreliable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource


enum class SensorAccuracy(
    val textResourceId: StringResource,
    val iconResourceId: DrawableResource
) {
    NO_CONTACT(
        Res.string.sensor_accuracy_no_contact,
        Res.drawable.ic_sensor_no_signal
    ),
    UNRELIABLE(
        Res.string.sensor_accuracy_unreliable,
        Res.drawable.ic_sensor_unreliable_signal
    ),
    LOW(
        Res.string.sensor_accuracy_low,
        Res.drawable.ic_sensor_low_signal
    ),
    MEDIUM(
        Res.string.sensor_accuracy_medium,
        Res.drawable.ic_sensor_medium_signal
    ),
    HIGH(
        Res.string.sensor_accuracy_high,
        Res.drawable.ic_sensor_high_signal
    )
}
