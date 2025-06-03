package com.skanderjabouzi.compassmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.skanderjabouzi.compassmp.handlers.rememberLocationPermissionController


class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionRequest()
            App()
        }
    }
}

@Composable
fun PermissionRequest() {
    val locationPermissionController = rememberLocationPermissionController()
    LaunchedEffect(Unit) {
            locationPermissionController.requestPermission { isGranted: Boolean -> // Explicitly typed
                if (isGranted) {
                    // Handle the case where permission is granted
                } else {
                    // Handle the case where permission is denied
                }
            }
        }
    }
