package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

@Composable
fun SettingsScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val store = LocalStore.current
    val prefs = LocalPrefs.current
    val context = LocalContext.current
    var confirmReset by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    var confirmSignOut by remember { mutableStateOf(false) }

    val exportText = buildString {
        appendLine("SimpleCRM Export"); appendLine()
        appendLine("Contacts:")
        store.contacts.forEach { appendLine("- ${it.name} (${it.company})") }
        appendLine(); appendLine("Deals:")
        store.deals.forEach { appendLine("- ${it.title}: ${it.value.toInt()} [${it.stage.label}]") }
    }.trimEnd()

    ScreenScaffold("Settings", onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Preferences") {
                ToggleRow("Notifications", prefs.notificationsEnabled) { prefs.notificationsEnabled = it }
                ToggleRow("Sync on Cellular", prefs.syncOnCellular) { prefs.syncOnCellular = it }
                ToggleRow("Biometric Lock", prefs.biometricLock) { prefs.biometricLock = it }
                ToggleRow("Dark Mode Override", prefs.darkModeOverride) { prefs.darkModeOverride = it }
                StepperRow(
                    "Sync every ${prefs.syncIntervalMinutes} min",
                    onMinus = { prefs.syncIntervalMinutes = (prefs.syncIntervalMinutes - 5).coerceAtLeast(5) },
                    onPlus = { prefs.syncIntervalMinutes = (prefs.syncIntervalMinutes + 5).coerceAtMost(60) },
                )
            }
            SectionCard("Regional") {
                PickerField("Default Currency", listOf("USD", "EUR", "GBP"), prefs.defaultCurrency, { it }, { prefs.defaultCurrency = it })
            }
            SectionCard("Account") {
                Row(Modifier.fillMaxWidth().clickable { onNavigate("profile") }) { LabeledRow("Name", store.profile.name) }
                LabeledRow("Plan", "Team")
                Row(Modifier.fillMaxWidth().clickable { onNavigate("about") }) { Text("About") }
            }
            SectionCard("Data") {
                TextButton({ context.shareText(exportText) }) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Export Data") }
                TextButton({ confirmReset = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("Reset All Data")
                }
            }
            EmbeddedWebPanel("Latest updates", "https://android-developers.googleblog.com/")
            TextButton({ confirmSignOut = true }, Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Sign Out") }
        }
    }

    if (confirmReset) ConfirmDialog("Reset all data?", "This cannot be undone.", "Reset", { showReset = true }, { confirmReset = false })
    if (showReset) InfoDialog("Data Reset", "This is a demo — no data was actually deleted.") { showReset = false }
    if (confirmSignOut) ConfirmDialog("Sign out of SimpleCRM?", null, "Sign Out", {}, { confirmSignOut = false })
}
