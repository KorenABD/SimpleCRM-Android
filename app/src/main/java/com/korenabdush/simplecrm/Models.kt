package com.korenabdush.simplecrm

import androidx.compose.ui.graphics.Color
import java.util.UUID

private fun newId() = UUID.randomUUID().toString()

enum class DealStage(val label: String, val color: Color) {
    Lead("Lead", Color(0xFF8E8E93)),
    Contacted("Contacted", Color(0xFF2F6FED)),
    Proposal("Proposal", Color(0xFFFF9500)),
    Won("Won", Color(0xFF34C759)),
    Lost("Lost", Color(0xFFFF3B30)),
}

enum class TaskPriority(val label: String, val color: Color) {
    Low("Low", Color(0xFF8E8E93)),
    Medium("Medium", Color(0xFFFF9500)),
    High("High", Color(0xFFFF3B30)),
}

data class Contact(
    val id: String = newId(),
    val name: String,
    val company: String,
    val email: String,
    val phone: String,
    val websiteUrl: String? = null,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val relationshipStrength: Float = 0.5f,
    val followUpInDays: Int = 7,
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val initials: String
        get() = name.split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().toString() }.uppercase()
}

data class Deal(
    val id: String = newId(),
    val title: String,
    val contactId: String,
    val value: Double,
    val stage: DealStage,
    val closeDate: Long,
    val winProbability: Float = 0.5f,
    val notes: String = "",
)

data class TaskItem(
    val id: String = newId(),
    val title: String,
    val notes: String = "",
    val dueDate: Long,
    val priority: TaskPriority = TaskPriority.Medium,
    val isDone: Boolean = false,
    val hasReminder: Boolean = false,
    val contactId: String? = null,
)

data class Note(
    val id: String = newId(),
    val title: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
)

data class Event(
    val id: String = newId(),
    val title: String,
    val start: Long,
    val end: Long,
    val isAllDay: Boolean = false,
    val contactId: String? = null,
)

data class NotificationItem(
    val id: String = newId(),
    val title: String,
    val subtitle: String,
    val date: Long,
    val isRead: Boolean = false,
    val isPinned: Boolean = false,
)

data class UserProfile(
    val name: String = "Koren Abdush",
    val email: String = "koren.abdush@example.com",
    val bio: String = "Head of Sales",
    val avatarIndex: Int = 0,
    val accentColor: Color = Color(0xFF2F6FED),
)
