package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.Note
import com.korenabdush.simplecrm.ui.*

private val Orange = Color(0xFFFF9500)

@Composable
fun NotesScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    val store = LocalStore.current
    var query by remember { mutableStateOf("") }
    var showNew by remember { mutableStateOf(false) }

    val notes = store.notes
        .filter { query.isBlank() || it.title.contains(query, true) || it.body.contains(query, true) }
        .sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.createdAt })

    ScreenScaffold("Notes", onBack, actions = { IconButton({ showNew = true }) { Icon(Icons.Default.Add, "New note") } }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                OutlinedTextField(
                    query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search notes") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true,
                )
            }
            if (notes.isEmpty()) item { EmptyState("No Notes", "Tap + to jot something down.") }
            items(notes, key = { it.id }) { note ->
                SwipeRow(
                    onDelete = { store.deleteNote(note.id) },
                    leading = LeadingAction(if (note.isPinned) "Unpin" else "Pin", Icons.Default.PushPin, Orange) { store.togglePinnedNote(note.id) },
                ) {
                    Column(Modifier.fillMaxWidth().clickable { onOpen(note.id) }.padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (note.isPinned) Icon(Icons.Default.PushPin, "Pinned", Modifier.size(14.dp), tint = Orange)
                            Text(note.title, fontWeight = FontWeight.Medium)
                        }
                        Text(note.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(formatDate(note.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    if (showNew) {
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        FormSheet("New Note", title.isNotBlank(), { showNew = false }, onSave = { store.addNote(Note(title = title, body = body)); showNew = false }) {
            Field("Title", title, { title = it })
            Field("Note", body, { body = it }, minLines = 6)
        }
    }
}

@Composable
fun NoteDetailScreen(id: String, onBack: () -> Unit) {
    val store = LocalStore.current
    val note = store.note(id)
    var menu by remember { mutableStateOf(false) }

    if (note == null) { // deleted
        LaunchedEffect(Unit) { onBack() }
        return
    }

    // Edits save as you type (the iOS app saves on disappear).
    ScreenScaffold("Note", onBack, actions = {
        Box {
            IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "Menu") }
            DropdownMenu(menu, { menu = false }) {
                DropdownMenuItem({ Text(if (note.isPinned) "Unpin" else "Pin") }, { menu = false; store.togglePinnedNote(id) }, leadingIcon = { Icon(Icons.Default.PushPin, null) })
                DropdownMenuItem({ Text("Delete") }, { menu = false; store.deleteNote(id) }, leadingIcon = { Icon(Icons.Default.Delete, null) })
            }
        }
    }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Field("Title", note.title, { v -> store.updateNote(note.copy(title = v)) })
            Field("Note", note.body, { v -> store.updateNote(note.copy(body = v)) }, minLines = 10)
        }
    }
}
