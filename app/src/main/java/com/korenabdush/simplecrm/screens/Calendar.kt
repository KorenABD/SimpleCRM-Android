package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.Event
import com.korenabdush.simplecrm.ui.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onBack: () -> Unit) {
    val store = LocalStore.current
    val zone = ZoneId.systemDefault()
    val state = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().atZone(zone).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )
    var showNew by remember { mutableStateOf(false) }

    // Picker reports UTC-midnight; convert to the same calendar day in the local zone.
    val selected = state.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().atTime(12, 0).atZone(zone).toInstant().toEpochMilli()
    } ?: System.currentTimeMillis()
    val events = store.eventsOn(selected)

    ScreenScaffold("Calendar", onBack, actions = { IconButton({ showNew = true }) { Icon(Icons.Default.Add, "New event") } }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item { DatePicker(state, title = null, headline = null, showModeToggle = false) }
            if (events.isEmpty()) item { EmptyState("No Events", "Nothing scheduled for this day.") }
            items(events, key = { it.id }) { event ->
                SwipeRow(onDelete = { store.deleteEvent(event.id) }) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.width(64.dp)) {
                            if (event.isAllDay) Text("All day", style = MaterialTheme.typography.bodySmall) else {
                                Text(formatTime(event.start), style = MaterialTheme.typography.bodySmall)
                                Text(formatTime(event.end), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Column {
                            Text(event.title, fontWeight = FontWeight.Medium)
                            store.contact(event.contactId)?.let {
                                Text(it.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNew) {
        var title by remember { mutableStateOf("") }
        var start by remember { mutableLongStateOf(selected) }
        var end by remember { mutableLongStateOf(selected + 3_600_000) }
        var allDay by remember { mutableStateOf(false) }
        var contactId by remember { mutableStateOf<String?>(null) }
        FormSheet("New Event", title.isNotBlank(), { showNew = false }, onSave = {
            store.addEvent(Event(title = title, start = start, end = end, isAllDay = allDay, contactId = contactId))
            showNew = false
        }) {
            Field("Title", title, { title = it })
            ToggleRow("All Day", allDay) { allDay = it }
            DateTimeField("Starts", start, withTime = !allDay) { start = it }
            DateTimeField("Ends", end, withTime = !allDay) { end = it }
            PickerField("Contact", listOf<String?>(null) + store.contacts.map { it.id }, contactId, { id -> store.contact(id)?.name ?: "None" }, { contactId = it })
        }
    }
}
