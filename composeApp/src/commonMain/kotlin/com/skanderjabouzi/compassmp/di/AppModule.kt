package com.skanderjabouzi.compassmp.di

import com.skanderjabouzi.compassmp.viewmodel.CompassViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Koin module for dependency injection
 */
val appModule = module {
    // ViewModels
    factory{ CompassViewModel() }
}
