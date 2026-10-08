package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

@Composable
private fun LinkRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    TextButton(onClick, Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
        Icon(icon, null); Spacer(Modifier.width(12.dp)); Text(label, Modifier.weight(1f))
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    ScreenScaffold("About", onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Verified, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                Text("SimpleCRM", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Version 1.0.0 (Build 1)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SectionCard("Links") {
                LinkRow("Website", Icons.Default.Language) { context.openUri("https://www.android.com") }
                LinkRow("Contact Support", Icons.Default.Email) { context.openUri("mailto:support@example.com", android.content.Intent.ACTION_SENDTO) }
                LinkRow("Privacy Policy", Icons.Default.PrivacyTip) { context.openUri("https://policies.google.com/privacy") }
                LinkRow("Terms of Service", Icons.Default.Description) { context.openUri("https://policies.google.com/terms") }
            }
            SectionCard("Rate & Share") {
                LinkRow("Rate on Google Play", Icons.Default.StarBorder) { context.openUri("https://play.google.com/store") }
                LinkRow("Share App", Icons.Default.Share) { context.shareText("Check out SimpleCRM — a lightweight CRM for Android.") }
            }
            EmbeddedWebPanel("Help Center", "https://support.google.com/android")
        }
    }
}
