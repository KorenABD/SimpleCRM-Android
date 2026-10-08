package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

private enum class ReportKind(val label: String, val url: String) {
    News("News", "https://www.bbc.com"),
    Reference("Reference", "https://www.wikipedia.org"),
    Tech("Tech", "https://www.android.com"),
}

@Composable
fun ReportsScreen(onBack: () -> Unit) {
    val store = LocalStore.current
    val context = LocalContext.current
    var kind by remember { mutableStateOf(ReportKind.News) }
    val summary = "Pipeline: ${store.deals.size} deals, ${store.contacts.size} contacts, ${store.openTasksCount} open tasks."

    ScreenScaffold("Reports", onBack, actions = {
        IconButton({ context.shareText(summary) }) { Icon(Icons.Default.Share, "Share") }
    }) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Segmented(ReportKind.entries, kind, { it.label }, { kind = it })
            SectionCard("Summary") { Text(summary, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            key(kind) { EmbeddedWebPanel("${kind.label} Feed", kind.url) }
        }
    }
}
