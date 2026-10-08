package com.korenabdush.simplecrm

import android.app.Application
import sdk.pendo.io.Pendo

class SimpleCRMApplication : Application() {
    /** Lives at process scope so data survives activity recreation (e.g. rotation). */
    val store = CRMStore()

    override fun onCreate() {
        super.onCreate()
        Pendo.setDebugMode(BuildConfig.DEBUG)
        Pendo.setup(this, BuildConfig.PENDO_API_KEY, null, null)
    }
}
