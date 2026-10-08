package com.korenabdush.simplecrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.korenabdush.simplecrm.ui.AppRoot
import sdk.pendo.io.Pendo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            val visitor = PendoDemoData.randomVisitor()
            Pendo.startSession(visitor.visitorId, visitor.accountId, visitor.visitorData, visitor.accountData)
        }

        val store = (application as SimpleCRMApplication).store
        val prefs = Prefs(applicationContext)
        setContent { AppRoot(store, prefs) }
    }
}
