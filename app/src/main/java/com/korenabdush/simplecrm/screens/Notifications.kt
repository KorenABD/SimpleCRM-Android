package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.NotificationItem
import com.korenabdush.simplecrm.ui.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val store = LocalStore.current
    val prefs = LocalPrefs.current
    var menuFor by remember { mutableStateOf<NotificationItem?>(null) }

    val sorted = store.notifications.sortedWith(compareByDescending<NotificationItem> { it.isPinned }.thenByDescending { it.date })

    ScreenScaffold("Notifications", onBack, actions = {
        TextButton({ store.markAllNotificationsRead() }, enabled = store.unreadNotificationsCount > 0) { Text("Mark All Read") }
    }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    ToggleRow("Do Not Disturb", prefs.doNotDisturb) { prefs.doNotDisturb = it }
                }
                HorizontalDivider()
            }
            if (sorted.isEmpty()) item { EmptyState("No Notifications") }
            items(sorted, key = { it.id }) { item ->
                SwipeRow(onDelete = { store.deleteNotification(item.id) }, leading = LeadingAction("Read", Icons.Default.MarkEmailRead, Color(0xFF2F6FED)) { store.markRead(item.id) }) {
                    Row(
                        Modifier.fillMaxWidth()
                            .combinedClickable(onClick = { store.markRead(item.id) }, onLongClick = { menuFor = item })
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(Modifier.padding(top = 6.dp).size(8.dp).background(if (item.isRead) Color.Transparent else Color(0xFF2F6FED), CircleShape))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (item.isPinned) Icon(Icons.Default.PushPin, "Pinned", Modifier.size(14.dp), tint = Color(0xFFFF9500))
                                Text(item.title, fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.SemiBold)
                            }
                            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatRelative(item.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }

    menuFor?.let { n ->
        AlertDialog(
            onDismissRequest = { menuFor = null },
            title = { Text(n.title) },
            text = {
                Column {
                    TextButton({ store.markRead(n.id); menuFor = null }) { Text("Mark Read") }
                    TextButton({ store.togglePinnedNotification(n.id); menuFor = null }) { Text(if (n.isPinned) "Unpin" else "Pin") }
                    TextButton({ store.deleteNotification(n.id); menuFor = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton({ menuFor = null }) { Text("Close") } },
        )
    }
}
