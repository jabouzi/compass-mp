package com.skanderjabouzi.salat.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin for dependency injection
 */
object KoinInitializer {
    /**
     * Initialize Koin with the app module
     */
    fun init(appDeclaration: KoinAppDeclaration = {}) {
        startKoin {
            appDeclaration()
            modules(appModule)
        }
    }
}