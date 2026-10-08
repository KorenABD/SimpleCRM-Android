package com.korenabdush.simplecrm.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.korenabdush.simplecrm.ui.*

@Composable
fun CompaniesScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    val store = LocalStore.current
    ScreenScaffold("Companies", onBack) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(store.companies, key = { it }) { company ->
                Row(
                    Modifier.fillMaxWidth().clickable { onOpen(company) }.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InitialsAvatar(company.take(2).uppercase(), 40.dp)
                    Column {
                        Text(company, fontWeight = FontWeight.Medium)
                        Text("${store.contactsAt(company).size} contact(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyDetailScreen(company: String, onBack: () -> Unit, onOpenContact: (String) -> Unit) {
    val store = LocalStore.current
    var employees by remember { mutableIntStateOf(120) }
    val contacts = store.contactsAt(company)
    val located = contacts.firstOrNull { it.latitude != null && it.longitude != null }

    ScreenScaffold(company, onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard("Overview") {
                LabeledRow("Company", company)
                StepperRow(
                    "Employees: $employees",
                    onMinus = { employees = (employees - 10).coerceAtLeast(1) },
                    onPlus = { employees = (employees + 10).coerceAtMost(10_000) },
                )
            }
            SectionCard("Contacts") {
                contacts.forEach { c ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onOpenContact(c.id) }.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        InitialsAvatar(c.initials, 32.dp)
                        Text(c.name)
                    }
                }
            }
            if (located != null) {
                EmbeddedWebPanel("Map Preview", osmEmbedUrl(located.latitude!!, located.longitude!!, 0.5))
            }
        }
    }
}
