package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

private val accentOptions = listOf(
    Color(0xFF2F6FED), Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFFFF9500), Color(0xFF009688), Color(0xFF34C759),
)

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val store = LocalStore.current
    var name by remember { mutableStateOf(store.profile.name) }
    var email by remember { mutableStateOf(store.profile.email) }
    var bio by remember { mutableStateOf(store.profile.bio) }
    var accent by remember { mutableStateOf(store.profile.accentColor) }
    var avatar by remember { mutableIntStateOf(store.profile.avatarIndex) }
    var saved by remember { mutableStateOf(false) }
    var confirmSignOut by remember { mutableStateOf(false) }

    ScreenScaffold("Profile", onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Avatar") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    avatarIcons.forEachIndexed { i, icon ->
                        IconButton({ avatar = i }) {
                            Icon(icon, null, Modifier.size(28.dp), tint = if (i == avatar) accent else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            SectionCard("Profile") {
                Field("Name", name, { name = it })
                Field("Email", email, { email = it })
                Field("Bio", bio, { bio = it }, minLines = 3)
            }
            SectionCard("Theme") {
                Text("Accent Color")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    accentOptions.forEach { c ->
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(c)
                                .border(if (c == accent) 3.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                .clickable { accent = c },
                        )
                    }
                }
            }
            Button({
                store.profile = store.profile.copy(name = name, email = email, bio = bio, accentColor = accent, avatarIndex = avatar)
                saved = true
            }, Modifier.fillMaxWidth()) { Text("Save Changes") }
            TextButton({ confirmSignOut = true }, Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Sign Out") }
        }
    }

    if (saved) InfoDialog("Profile Saved", onDismiss = { saved = false })
    if (confirmSignOut) ConfirmDialog("Sign out of SimpleCRM?", null, "Sign Out", onConfirm = onBack, onDismiss = { confirmSignOut = false })
}
