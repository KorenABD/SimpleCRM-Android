package com.korenabdush.simplecrm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

private const val HOUR = 60L * 60 * 1000
private const val DAY = 24 * HOUR

/** In-memory sample data, mirroring the iOS `CRMStore`. */
class CRMStore {
    val contacts = mutableStateListOf<Contact>()
    val deals = mutableStateListOf<Deal>()
    val tasks = mutableStateListOf<TaskItem>()
    val notes = mutableStateListOf<Note>()
    val events = mutableStateListOf<Event>()
    val notifications = mutableStateListOf<NotificationItem>()
    var profile by mutableStateOf(UserProfile())

    init {
        val now = System.currentTimeMillis()
        val acme = Contact(
            name = "Jane Cooper", company = "Acme Corp", email = "jane.cooper@acme.example",
            phone = "+1 (555) 010-2938", websiteUrl = "https://www.android.com",
            notes = "Met at the Q3 conference. Interested in the enterprise plan.",
            isFavorite = true, relationshipStrength = 0.8f, followUpInDays = 3,
            latitude = 37.3349, longitude = -122.0090,
        )
        val globex = Contact(
            name = "Devon Lane", company = "Globex Inc.", email = "devon.lane@globex.example",
            phone = "+1 (555) 019-4471", websiteUrl = "https://www.wikipedia.org",
            notes = "Champion on the buying committee. Prefers email over calls.",
            relationshipStrength = 0.6f, followUpInDays = 10,
            latitude = 40.7484, longitude = -73.9857,
        )
        val initech = Contact(
            name = "Cameron Wright", company = "Initech", email = "cameron.wright@initech.example",
            phone = "+1 (555) 088-3321", websiteUrl = "https://www.bbc.com",
            notes = "Renewal is up in Q1. Needs a demo of the new reporting features.",
            relationshipStrength = 0.4f, followUpInDays = 14,
            latitude = 51.5074, longitude = -0.1278,
        )
        contacts += listOf(acme, globex, initech)

        deals += listOf(
            Deal(title = "Enterprise upgrade", contactId = acme.id, value = 42_000.0, stage = DealStage.Proposal, closeDate = now + 14 * DAY, winProbability = 0.65f, notes = "Waiting on legal review of the MSA."),
            Deal(title = "Annual renewal", contactId = initech.id, value = 18_500.0, stage = DealStage.Contacted, closeDate = now + 30 * DAY, winProbability = 0.4f, notes = "Needs the new reporting demo before committing."),
            Deal(title = "Add-on seats", contactId = globex.id, value = 6_200.0, stage = DealStage.Won, closeDate = now - 5 * DAY, winProbability = 1f, notes = "Closed-won, seats provisioned."),
            Deal(title = "Starter package", contactId = globex.id, value = 3_000.0, stage = DealStage.Lead, closeDate = now + 45 * DAY, winProbability = 0.2f),
        )

        tasks += listOf(
            TaskItem(title = "Follow up with Jane Cooper", dueDate = now + DAY, priority = TaskPriority.High, hasReminder = true, contactId = acme.id),
            TaskItem(title = "Prepare renewal proposal", notes = "Include the new reporting tier pricing.", dueDate = now + 2 * DAY, priority = TaskPriority.High, contactId = initech.id),
            TaskItem(title = "Send onboarding docs", dueDate = now + 4 * DAY, priority = TaskPriority.Medium, contactId = globex.id),
            TaskItem(title = "Quarterly pipeline review", dueDate = now + 6 * DAY, priority = TaskPriority.Low, isDone = true),
        )

        notes += listOf(
            Note(title = "Q3 conference takeaways", body = "Jane's team is expanding into EMEA next year. Worth a proactive check-in.", isPinned = true),
            Note(title = "Pricing objections", body = "Initech pushed back on per-seat pricing. Consider a flat enterprise tier.", createdAt = now - 3 * DAY),
        )

        events += listOf(
            Event(title = "Discovery call — Globex", start = now + 3 * HOUR, end = now + 4 * HOUR, contactId = globex.id),
            Event(title = "Renewal review — Initech", start = now + DAY + 5 * HOUR, end = now + DAY + 6 * HOUR, contactId = initech.id),
            Event(title = "Team pipeline sync", start = now + 2 * DAY, end = now + 2 * DAY + HOUR),
        )

        notifications += listOf(
            NotificationItem(title = "Deal stage changed", subtitle = "\"Add-on seats\" moved to Won", date = now - 30 * 60 * 1000),
            NotificationItem(title = "Task due tomorrow", subtitle = "Follow up with Jane Cooper", date = now - 2 * HOUR),
            NotificationItem(title = "New note added", subtitle = "Pricing objections", date = now - DAY, isRead = true),
        )
    }

    // Contacts
    fun contact(id: String?): Contact? = contacts.firstOrNull { it.id == id }
    fun addContact(c: Contact) { contacts += c }
    fun updateContact(c: Contact) = replace(contacts, c) { it.id == c.id }
    fun deleteContact(id: String) { contacts.removeAll { it.id == id } }
    fun updateContact(id: String, f: (Contact) -> Contact) = contact(id)?.let { updateContact(f(it)) }
    val companies: List<String> get() = contacts.map { it.company }.distinct().sorted()
    fun contactsAt(company: String) = contacts.filter { it.company == company }

    // Deals
    fun deal(id: String?): Deal? = deals.firstOrNull { it.id == id }
    fun dealsFor(contactId: String) = deals.filter { it.contactId == contactId }
    fun addDeal(d: Deal) { deals += d }
    fun deleteDeal(id: String) { deals.removeAll { it.id == id } }
    fun updateDeal(id: String, f: (Deal) -> Deal) = deal(id)?.let { d -> replace(deals, f(d)) { it.id == id } }
    val totalPipelineValue: Double get() = deals.filter { it.stage != DealStage.Won && it.stage != DealStage.Lost }.sumOf { it.value }
    val wonValue: Double get() = deals.filter { it.stage == DealStage.Won }.sumOf { it.value }

    // Tasks
    fun addTask(t: TaskItem) { tasks += t }
    fun deleteTask(id: String) { tasks.removeAll { it.id == id } }
    fun toggleTaskDone(id: String) = tasks.indexOfFirst { it.id == id }.takeIf { it >= 0 }?.let { tasks[it] = tasks[it].copy(isDone = !tasks[it].isDone) }
    val openTasksCount: Int get() = tasks.count { !it.isDone }

    // Notes
    fun note(id: String?): Note? = notes.firstOrNull { it.id == id }
    fun addNote(n: Note) { notes += n }
    fun updateNote(n: Note) = replace(notes, n) { it.id == n.id }
    fun deleteNote(id: String) { notes.removeAll { it.id == id } }
    fun togglePinnedNote(id: String) = note(id)?.let { updateNote(it.copy(isPinned = !it.isPinned)) }

    // Events
    fun addEvent(e: Event) { events += e }
    fun deleteEvent(id: String) { events.removeAll { it.id == id } }
    fun eventsOn(dayMillis: Long): List<Event> {
        fun key(ms: Long) = Calendar.getInstance().apply { timeInMillis = ms }.let { it.get(Calendar.YEAR) * 1000 + it.get(Calendar.DAY_OF_YEAR) }
        val k = key(dayMillis)
        return events.filter { key(it.start) == k }.sortedBy { it.start }
    }

    // Notifications
    val unreadNotificationsCount: Int get() = notifications.count { !it.isRead }
    fun markRead(id: String) = notifications.indexOfFirst { it.id == id }.takeIf { it >= 0 }?.let { notifications[it] = notifications[it].copy(isRead = true) }
    fun togglePinnedNotification(id: String) = notifications.indexOfFirst { it.id == id }.takeIf { it >= 0 }?.let { notifications[it] = notifications[it].copy(isPinned = !notifications[it].isPinned) }
    fun deleteNotification(id: String) { notifications.removeAll { it.id == id } }
    fun markAllNotificationsRead() { for (i in notifications.indices) notifications[i] = notifications[i].copy(isRead = true) }

    private inline fun <T> replace(list: MutableList<T>, item: T, match: (T) -> Boolean) {
        val i = list.indexOfFirst(match)
        if (i >= 0) list[i] = item
    }
}
