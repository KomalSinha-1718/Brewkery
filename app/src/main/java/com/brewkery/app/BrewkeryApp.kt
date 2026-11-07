package com.brewkery.app

import android.app.Application

/** Holds the manually-wired dependency graph (kept simple on purpose — see README for the Hilt trade-off). */
class BrewkeryApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
