package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.DealStage
import com.korenabdush.simplecrm.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val store = LocalStore.current
    var range by remember { mutableStateOf("Week") }
    var refreshing by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ScreenScaffold("Dashboard") { padding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { scope.launch { refreshing = true; delay(800); refreshing = false } },
            modifier = Modifier.padding(padding),
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Welcome back,", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(store.profile.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }

                Segmented(listOf("Week", "Month", "Year"), range, { it }, { range = it })

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Contacts", "${store.contacts.size}", Icons.Default.People, Color(0xFF2F6FED), Modifier.weight(1f))
                        StatCard("Open Tasks", "${store.openTasksCount}", Icons.Default.Checklist, Color(0xFFFF9500), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Pipeline", formatCurrency(store.totalPipelineValue), Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF9C27B0), Modifier.weight(1f))
                        StatCard("Won ($range)", formatCurrency(store.wonValue), Icons.Default.EmojiEvents, Color(0xFF34C759), Modifier.weight(1f))
                    }
                }

                SectionCard("Deals by Stage") {
                    SimpleBarChart(DealStage.entries.map { s -> s.label to store.deals.count { it.stage == s }.toDouble() })
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActionTile("Contact", Icons.Default.PersonAdd, Modifier.weight(1f)) { sheet = "contact" }
                        ActionTile("Deal", Icons.Default.MonetizationOn, Modifier.weight(1f)) { sheet = "deal" }
                        ActionTile("Task", Icons.Default.CheckCircle, Modifier.weight(1f)) { sheet = "task" }
                    }
                }

                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Recent Activity", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        TextButton({ showAll = !showAll }) { Text(if (showAll) "Show Less" else "Show All") }
                    }
                    val items = store.notifications.take(if (showAll) store.notifications.size else 2)
                    items.forEachIndexed { i, item ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column {
                                Text(item.title, fontWeight = FontWeight.Medium)
                                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (i < items.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }

    when (sheet) {
        "contact" -> NewContactSheet { sheet = null }
        "deal" -> NewDealSheet { sheet = null }
        "task" -> NewTaskSheet { sheet = null }
    }
}
