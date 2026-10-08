package com.korenabdush.simplecrm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** SharedPreferences-backed settings, the equivalent of the iOS `@AppStorage` keys. */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("simplecrm", Context.MODE_PRIVATE)

    var doNotDisturb by bool("doNotDisturb", false)
    var notificationsEnabled by bool("notificationsEnabled", true)
    var syncOnCellular by bool("syncOnCellular", false)
    var biometricLock by bool("biometricLock", false)
    var darkModeOverride by bool("darkModeOverride", false)
    var defaultCurrency by string("defaultCurrency", "USD")
    var syncIntervalMinutes by int("syncIntervalMinutes", 15)

    private fun bool(key: String, default: Boolean) = PrefDelegate(sp.getBoolean(key, default)) { sp.edit().putBoolean(key, it).apply() }
    private fun string(key: String, default: String) = PrefDelegate(sp.getString(key, default) ?: default) { sp.edit().putString(key, it).apply() }
    private fun int(key: String, default: Int) = PrefDelegate(sp.getInt(key, default)) { sp.edit().putInt(key, it).apply() }
}

class PrefDelegate<T>(initial: T, private val persist: (T) -> Unit) {
    private var state by mutableStateOf(initial)
    operator fun getValue(thisRef: Any?, property: Any?): T = state
    operator fun setValue(thisRef: Any?, property: Any?, value: T) {
        state = value
        persist(value)
    }
}
