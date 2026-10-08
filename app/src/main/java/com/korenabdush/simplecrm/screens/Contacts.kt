package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.Contact
import com.korenabdush.simplecrm.ui.*

private enum class ContactSort(val label: String) { Name("Name"), Company("Company") }

private val Gold = Color(0xFFFFC107)

@Composable
fun ContactsScreen(onOpen: (String) -> Unit) {
    val store = LocalStore.current
    var query by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf(ContactSort.Name) }
    var showNew by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Contact?>(null) }
    var contextFor by remember { mutableStateOf<Contact?>(null) }
    val context = LocalContext.current

    val contacts = store.contacts
        .filter { !favoritesOnly || it.isFavorite }
        .filter { query.isBlank() || it.name.contains(query, true) || it.company.contains(query, true) }
        .let { list -> if (sort == ContactSort.Name) list.sortedBy { it.name } else list.sortedBy { it.company } }

    ScreenScaffold("Contacts", actions = {
        Box {
            IconButton({ menu = true }) { Icon(Icons.Default.SwapVert, "Sort") }
            DropdownMenu(menu, { menu = false }) {
                ContactSort.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        leadingIcon = { if (sort == option) Icon(Icons.Default.Check, null) },
                        onClick = { sort = option; menu = false },
                    )
                }
            }
        }
        IconButton({ showNew = true }) { Icon(Icons.Default.Add, "New contact") }
    }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                OutlinedTextField(
                    query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search contacts") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true,
                )
                Box(Modifier.padding(horizontal = 16.dp)) { ToggleRow("Favorites Only", favoritesOnly, { favoritesOnly = it }) }
            }
            items(contacts, key = { it.id }) { contact ->
                SwipeRow(
                    onDelete = { pendingDelete = contact },
                    leading = LeadingAction(if (contact.isFavorite) "Unfavorite" else "Favorite", Icons.Default.Star, Gold) {
                        store.updateContact(contact.id) { it.copy(isFavorite = !it.isFavorite) }
                    },
                ) {
                    ContactRow(contact, onClick = { onOpen(contact.id) }, onLongClick = { contextFor = contact })
                }
            }
            if (contacts.isEmpty()) item { EmptyState("No Contacts", "Try a different search or add a new contact.") }
        }
    }

    if (showNew) NewContactSheet { showNew = false }
    pendingDelete?.let { c ->
        ConfirmDialog("Delete ${c.name}?", null, "Delete", { store.deleteContact(c.id) }, { pendingDelete = null })
    }
    contextFor?.let { c ->
        AlertDialog(
            onDismissRequest = { contextFor = null },
            title = { Text(c.name) },
            text = {
                Column {
                    TextButton({ store.updateContact(c.id) { it.copy(isFavorite = !it.isFavorite) }; contextFor = null }) {
                        Text(if (c.isFavorite) "Remove Favorite" else "Add Favorite")
                    }
                    TextButton({ context.openUri("tel:${c.phone.filter { it.isDigit() }}", android.content.Intent.ACTION_DIAL); contextFor = null }) { Text("Call") }
                    TextButton({ pendingDelete = c; contextFor = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton({ contextFor = null }) { Text("Close") } },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContactRow(contact: Contact, onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InitialsAvatar(contact.initials)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(contact.name, fontWeight = FontWeight.Medium)
                if (contact.isFavorite) Icon(Icons.Default.Star, "Favorite", Modifier.size(14.dp), tint = Gold)
            }
            Text(contact.company, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ContactDetailScreen(id: String, onBack: () -> Unit, onOpenDeal: (String) -> Unit) {
    val store = LocalStore.current
    val context = LocalContext.current
    val contact = store.contact(id)
    var menu by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showTask by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    if (contact == null) { // deleted
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val deals = store.dealsFor(id)
    val strengthLabel = when {
        contact.relationshipStrength < 0.34f -> "Cold"
        contact.relationshipStrength < 0.67f -> "Warming Up"
        else -> "Strong"
    }

    ScreenScaffold(contact.name, onBack, actions = {
        Box {
            IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "Menu") }
            DropdownMenu(menu, { menu = false }) {
                DropdownMenuItem({ Text("Edit") }, { menu = false; showEdit = true }, leadingIcon = { Icon(Icons.Default.Edit, null) })
                DropdownMenuItem({ Text("New Task") }, { menu = false; showTask = true }, leadingIcon = { Icon(Icons.Default.CheckCircle, null) })
                DropdownMenuItem({ Text("Delete Contact") }, { menu = false; confirmDelete = true }, leadingIcon = { Icon(Icons.Default.Delete, null) })
            }
        }
    }) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InitialsAvatar(contact.initials, 64.dp)
                Column(Modifier.weight(1f)) {
                    Text(contact.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(contact.company, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton({ store.updateContact(id) { it.copy(isFavorite = !it.isFavorite) } }) {
                    Icon(if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder, "Favorite", tint = Gold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile("Call", Icons.Default.Call, Modifier.weight(1f)) {
                    context.openUri("tel:${contact.phone.filter { it.isDigit() }}", android.content.Intent.ACTION_DIAL)
                }
                ActionTile("Email", Icons.Default.Email, Modifier.weight(1f)) { context.openUri("mailto:${contact.email}", android.content.Intent.ACTION_SENDTO) }
                ActionTile("Share", Icons.Default.Share, Modifier.weight(1f)) {
                    context.shareText("${contact.name} — ${contact.company}\n${contact.email}\n${contact.phone}")
                }
            }

            SectionCard("Details") {
                LabeledRow("Email", contact.email)
                LabeledRow("Phone", contact.phone)
            }

            SectionCard("Relationship Strength") {
                Slider(contact.relationshipStrength, { v -> store.updateContact(id) { it.copy(relationshipStrength = v) } }, valueRange = 0f..1f)
                Text("${(contact.relationshipStrength * 100).toInt()}% — $strengthLabel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            SectionCard("Follow-up") {
                val d = contact.followUpInDays
                StepperRow(
                    "In $d day${if (d == 1) "" else "s"}",
                    onMinus = { store.updateContact(id) { it.copy(followUpInDays = (it.followUpInDays - 1).coerceAtLeast(1)) } },
                    onPlus = { store.updateContact(id) { it.copy(followUpInDays = (it.followUpInDays + 1).coerceAtMost(60)) } },
                )
            }

            if (contact.notes.isNotEmpty()) SectionCard("Notes") { Text(contact.notes, style = MaterialTheme.typography.bodyMedium) }

            if (deals.isNotEmpty()) SectionCard("Deals") {
                deals.forEachIndexed { i, deal ->
                    DealRow(deal, null) { onOpenDeal(deal.id) }
                    if (i < deals.lastIndex) HorizontalDivider()
                }
            }

            if (contact.latitude != null && contact.longitude != null) {
                EmbeddedWebPanel("Location", osmEmbedUrl(contact.latitude, contact.longitude))
            }
            contact.websiteUrl?.let { EmbeddedWebPanel("Company Website", it) }
        }
    }

    if (showEdit) EditContactSheet(contact) { showEdit = false }
    if (showTask) NewTaskSheet { showTask = false }
    if (confirmDelete) {
        ConfirmDialog(
            "Delete ${contact.name}?", "This will also remove this contact's association with any deals.", "Delete",
            onConfirm = { store.deleteContact(id) }, onDismiss = { confirmDelete = false },
        )
    }
}

@Composable
fun NewContactSheet(onDismiss: () -> Unit) {
    val store = LocalStore.current
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var favorite by remember { mutableStateOf(false) }

    FormSheet("New Contact", name.isNotBlank(), onDismiss, onSave = {
        store.addContact(Contact(name = name, company = company, email = email, phone = phone, isFavorite = favorite))
        onDismiss()
    }) {
        Field("Name", name, { name = it })
        Field("Company", company, { company = it })
        Field("Email", email, { email = it })
        Field("Phone", phone, { phone = it })
        ToggleRow("Mark as Favorite", favorite) { favorite = it }
    }
}

@Composable
fun EditContactSheet(contact: Contact, onDismiss: () -> Unit) {
    val store = LocalStore.current
    var name by remember { mutableStateOf(contact.name) }
    var company by remember { mutableStateOf(contact.company) }
    var email by remember { mutableStateOf(contact.email) }
    var phone by remember { mutableStateOf(contact.phone) }
    var notes by remember { mutableStateOf(contact.notes) }

    FormSheet("Edit Contact", name.isNotBlank(), onDismiss, onSave = {
        store.updateContact(contact.id) { it.copy(name = name, company = company, email = email, phone = phone, notes = notes) }
        onDismiss()
    }) {
        Field("Name", name, { name = it })
        Field("Company", company, { company = it })
        Field("Email", email, { email = it })
        Field("Phone", phone, { phone = it })
        Field("Notes", notes, { notes = it }, minLines = 3)
    }
}
