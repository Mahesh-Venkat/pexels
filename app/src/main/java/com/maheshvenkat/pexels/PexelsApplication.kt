package com.maheshvenkat.pexels

import android.app.Application
import timber.log.Timber

class PexelsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}