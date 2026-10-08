package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.TaskItem
import com.korenabdush.simplecrm.TaskPriority
import com.korenabdush.simplecrm.ui.*

private enum class TaskFilter(val label: String) { All("All"), Open("Open"), Done("Done") }

@Composable
fun TasksScreen() {
    val store = LocalStore.current
    var filter by remember { mutableStateOf(TaskFilter.Open) }
    var priority by remember { mutableStateOf<TaskPriority?>(null) }
    var showNew by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }

    val tasks = store.tasks
        .filter { when (filter) { TaskFilter.All -> true; TaskFilter.Open -> !it.isDone; TaskFilter.Done -> it.isDone } }
        .filter { priority == null || it.priority == priority }
        .sortedBy { it.dueDate }

    ScreenScaffold("Tasks", actions = {
        Box {
            IconButton({ menu = true }) { Icon(Icons.Default.FilterList, "Filter priority") }
            DropdownMenu(menu, { menu = false }) {
                DropdownMenuItem({ Text("All Priorities") }, { priority = null; menu = false })
                TaskPriority.entries.forEach { p -> DropdownMenuItem({ Text(p.label) }, { priority = p; menu = false }) }
            }
        }
        IconButton({ showNew = true }) { Icon(Icons.Default.Add, "New task") }
    }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                Segmented(TaskFilter.entries, filter, { it.label }, { filter = it }, Modifier.padding(16.dp))
            }
            if (tasks.isEmpty()) item { EmptyState("No Tasks", "Nothing here — enjoy the quiet.") }
            items(tasks, key = { it.id }) { task ->
                SwipeRow(
                    onDelete = { store.deleteTask(task.id) },
                    leading = LeadingAction(if (task.isDone) "Reopen" else "Done", Icons.Default.Check, Color(0xFF34C759)) { store.toggleTaskDone(task.id) },
                ) { TaskRow(task) }
            }
        }
    }

    if (showNew) NewTaskSheet { showNew = false }
}

@Composable
private fun TaskRow(task: TaskItem) {
    val store = LocalStore.current
    val contact = store.contact(task.contactId)
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.Top) {
        IconButton({ store.toggleTaskDone(task.id) }) {
            Icon(
                if (task.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, "Toggle done",
                tint = if (task.isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(Modifier.weight(1f).padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                task.title,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(formatDate(task.dueDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (task.hasReminder) Icon(Icons.Default.Notifications, "Reminder", Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (contact != null) Text(contact.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Box(Modifier.padding(top = 12.dp, end = 8.dp)) { PillBadge(task.priority.label, task.priority.color) }
    }
}

@Composable
fun NewTaskSheet(onDismiss: () -> Unit) {
    val store = LocalStore.current
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var due by remember { mutableLongStateOf(System.currentTimeMillis() + 24L * 60 * 60 * 1000) }
    var priority by remember { mutableStateOf(TaskPriority.Medium) }
    var reminder by remember { mutableStateOf(false) }
    var contactId by remember { mutableStateOf<String?>(null) }

    FormSheet("New Task", title.isNotBlank(), onDismiss, onSave = {
        store.addTask(TaskItem(title = title, notes = notes, dueDate = due, priority = priority, hasReminder = reminder, contactId = contactId))
        onDismiss()
    }) {
        Field("Title", title, { title = it })
        DateTimeField("Due", due, withTime = true) { due = it }
        Segmented(TaskPriority.entries, priority, { it.label }, { priority = it })
        PickerField("Contact", listOf<String?>(null) + store.contacts.map { it.id }, contactId, { id -> store.contact(id)?.name ?: "None" }, { contactId = it })
        Field("Notes", notes, { notes = it }, minLines = 3)
        ToggleRow("Remind Me", reminder) { reminder = it }
    }
}
