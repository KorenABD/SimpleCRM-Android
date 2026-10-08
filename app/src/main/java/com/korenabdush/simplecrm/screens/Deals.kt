package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.Deal
import com.korenabdush.simplecrm.DealStage
import com.korenabdush.simplecrm.ui.*

private enum class DealSort(val label: String) { CloseDate("Close Date"), Value("Value"), Name("Name") }

@Composable
fun DealsScreen(onOpen: (String) -> Unit) {
    val store = LocalStore.current
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(DealSort.CloseDate) }
    var showNew by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Deal?>(null) }

    fun filtered(deals: List<Deal>): List<Deal> {
        val list = deals.filter { query.isBlank() || it.title.contains(query, true) }
        return when (sort) {
            DealSort.CloseDate -> list.sortedBy { it.closeDate }
            DealSort.Value -> list.sortedByDescending { it.value }
            DealSort.Name -> list.sortedBy { it.title }
        }
    }
    val groups = DealStage.entries.mapNotNull { stage ->
        filtered(store.deals.filter { it.stage == stage }).takeIf { it.isNotEmpty() }?.let { stage to it }
    }

    ScreenScaffold("Deals", actions = {
        Box {
            IconButton({ menu = true }) { Icon(Icons.Default.SwapVert, "Sort") }
            DropdownMenu(menu, { menu = false }) {
                DealSort.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        leadingIcon = { if (sort == option) Icon(Icons.Default.Check, null) },
                        onClick = { sort = option; menu = false },
                    )
                }
            }
        }
        IconButton({ showNew = true }) { Icon(Icons.Default.Add, "New deal") }
    }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                OutlinedTextField(
                    query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search deals") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true,
                )
            }
            if (groups.isEmpty()) item { EmptyState("No Deals", "Try a different search or add a new deal.") }
            groups.forEach { (stage, deals) ->
                item(key = "header-${stage.name}") {
                    Text(stage.label, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(deals, key = { it.id }) { deal ->
                    SwipeRow(
                        onDelete = { pendingDelete = deal },
                        leading = if (deal.stage != DealStage.Won) LeadingAction("Won", Icons.Default.EmojiEvents, Color(0xFF34C759)) {
                            store.updateDeal(deal.id) { it.copy(stage = DealStage.Won) }
                        } else null,
                    ) {
                        Box(Modifier.padding(horizontal = 16.dp)) {
                            DealRow(deal, store.contact(deal.contactId)?.company) { onOpen(deal.id) }
                        }
                    }
                }
            }
        }
    }

    if (showNew) NewDealSheet { showNew = false }
    pendingDelete?.let { d -> ConfirmDialog("Delete ${d.title}?", null, "Delete", { store.deleteDeal(d.id) }, { pendingDelete = null }) }
}

@Composable
fun DealDetailScreen(id: String, onBack: () -> Unit, onOpenContact: (String) -> Unit) {
    val store = LocalStore.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val deal = store.deal(id)
    var confirmDelete by remember { mutableStateOf(false) }

    if (deal == null) { // deleted
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val contact = store.contact(deal.contactId)

    ScreenScaffold(deal.title, onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Deal") {
                LabeledRow("Title", deal.title)
                LabeledRow("Value", formatCurrency(deal.value))
                LabeledRow("Close Date", formatDate(deal.closeDate))
            }

            SectionCard("Stage") {
                Segmented(DealStage.entries, deal.stage, { it.label }, { s -> store.updateDeal(id) { it.copy(stage = s) } })
            }

            SectionCard("Win Probability") {
                Slider(
                    deal.winProbability, { v -> store.updateDeal(id) { it.copy(winProbability = (Math.round(v * 20) / 20f)) } },
                    valueRange = 0f..1f, steps = 19,
                )
                Text("${(deal.winProbability * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (contact != null) SectionCard("Contact") {
                Row(
                    Modifier.fillMaxWidth().clickable { onOpenContact(contact.id) },
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InitialsAvatar(contact.initials, 36.dp)
                    Column {
                        Text(contact.name)
                        Text(contact.company, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            SectionCard("Notes") {
                OutlinedTextField(deal.notes, { v -> store.updateDeal(id) { it.copy(notes = v) } }, Modifier.fillMaxWidth(), minLines = 4)
            }

            SectionCard {
                TextButton({ context.shareText("${deal.title} — ${formatCurrency(deal.value)} (${deal.stage.label})") }) {
                    Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Share Deal Summary")
                }
                TextButton({ confirmDelete = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("Delete Deal")
                }
            }
        }
    }

    if (confirmDelete) ConfirmDialog("Delete ${deal.title}?", null, "Delete", { store.deleteDeal(id) }, { confirmDelete = false })
}

@Composable
fun NewDealSheet(onDismiss: () -> Unit) {
    val store = LocalStore.current
    var title by remember { mutableStateOf("") }
    var contactId by remember { mutableStateOf<String?>(null) }
    var value by remember { mutableDoubleStateOf(5_000.0) }
    var stage by remember { mutableStateOf(DealStage.Lead) }
    var closeDate by remember { mutableLongStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }

    FormSheet("New Deal", title.isNotBlank() && contactId != null, onDismiss, onSave = {
        store.addDeal(Deal(title = title, contactId = contactId!!, value = value, stage = stage, closeDate = closeDate))
        onDismiss()
    }) {
        Field("Title", title, { title = it })
        PickerField("Contact", listOf<String?>(null) + store.contacts.map { it.id }, contactId, { id -> store.contact(id)?.name ?: "None" }, { contactId = it })
        StepperRow(
            formatCurrency(value),
            onMinus = { value = (value - 500).coerceAtLeast(0.0) },
            onPlus = { value = (value + 500).coerceAtMost(500_000.0) },
        )
        Segmented(DealStage.entries, stage, { it.label }, { stage = it })
        DateTimeField("Close Date", closeDate, withTime = false) { closeDate = it }
    }
}
