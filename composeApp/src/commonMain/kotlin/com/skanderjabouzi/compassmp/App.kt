package com.skanderjabouzi.compassmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.skanderjabouzi.compassmp.screens.MainScreen
import com.skanderjabouzi.compassmp.theme.AppTheme
import com.skanderjabouzi.salat.di.KoinInitializer
import org.jetbrains.compose.ui.tooling.preview.Preview

private val koinInitialized by lazy {
    KoinInitializer.init()
    true
}

@Preview
@Composable
internal fun App() = AppTheme {
    remember { koinInitialized }
    MainScreen()
}
