package com.skanderjabouzi.compassmp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skanderjabouzi.compassmp.model.SensorAccuracy
import com.skanderjabouzi.compassmp.viewmodel.CompassViewModel
import compass_mp.composeapp.generated.resources.Res
import compass_mp.composeapp.generated.resources.img_sensor_calibration_explanation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// SensorStatusDialog.kt
@Composable
fun SensorStatusDialog(
    compassViewModel: CompassViewModel,
    onDismiss: () -> Unit
) {
    val sensorAccuracy by compassViewModel.sensorAccuracy.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sensor Status") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.img_sensor_calibration_explanation),
                        contentDescription = null,
                        tint = when (sensorAccuracy) {
                            SensorAccuracy.NO_CONTACT, SensorAccuracy.UNRELIABLE, SensorAccuracy.LOW ->
                                MaterialTheme.colorScheme.error

                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(sensorAccuracy.textResourceId))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}