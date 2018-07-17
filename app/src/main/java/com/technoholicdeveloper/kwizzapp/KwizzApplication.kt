package com.technoholicdeveloper.kwizzapp

import android.app.Application
import timber.log.Timber

class KwizzApplication : Application() {
    companion object {


    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}