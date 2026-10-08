package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

val avatarIcons: List<ImageVector> = listOf(
    Icons.Default.AccountCircle, Icons.Default.Verified, Icons.Default.Stars,
    Icons.Default.Bolt, Icons.Default.Eco, Icons.Default.LocalFireDepartment,
)

@Composable
private fun NavRow(label: String, icon: ImageVector, trailing: @Composable () -> Unit = {}, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Text(label, Modifier.weight(1f))
        trailing()
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MoreScreen(navigate: (String) -> Unit) {
    val store = LocalStore.current
    ScreenScaffold("More") { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                Row(
                    Modifier.fillMaxWidth().clickable { navigate("profile") }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(avatarIcons[store.profile.avatarIndex], null, Modifier.size(40.dp), tint = store.profile.accentColor)
                    Column {
                        Text(store.profile.name, style = MaterialTheme.typography.titleMedium)
                        Text(store.profile.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HorizontalDivider()
                Text("Workspace", Modifier.padding(16.dp, 16.dp, 16.dp, 4.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                NavRow("Companies", Icons.Default.Business) { navigate("companies") }
                NavRow("Notes", Icons.Default.StickyNote2) { navigate("notes") }
                NavRow("Calendar", Icons.Default.CalendarMonth) { navigate("calendar") }
                NavRow("Reports", Icons.Default.Assessment) { navigate("reports") }
                NavRow("Notifications", Icons.Default.Notifications, trailing = {
                    if (store.unreadNotificationsCount > 0) Badge { Text("${store.unreadNotificationsCount}") }
                }) { navigate("notifications") }
                HorizontalDivider(Modifier.padding(top = 8.dp))
                NavRow("Settings", Icons.Default.Settings) { navigate("settings") }
                NavRow("About", Icons.Default.Info) { navigate("about") }
            }
        }
    }
}
