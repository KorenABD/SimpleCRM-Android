package com.korenabdush.simplecrm.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.korenabdush.simplecrm.CRMStore
import com.korenabdush.simplecrm.Prefs
import com.korenabdush.simplecrm.screens.*

val LocalStore = staticCompositionLocalOf<CRMStore> { error("CRMStore not provided") }
val LocalPrefs = staticCompositionLocalOf<Prefs> { error("Prefs not provided") }

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("dashboard", "Dashboard", Icons.Default.GridView),
    Tab("contacts", "Contacts", Icons.Default.AccountCircle),
    Tab("deals", "Deals", Icons.AutoMirrored.Filled.TrendingUp),
    Tab("tasks", "Tasks", Icons.Default.Checklist),
    Tab("more", "More", Icons.Default.MoreHoriz),
)

@Composable
fun AppRoot(store: CRMStore, prefs: Prefs) {
    CompositionLocalProvider(LocalStore provides store, LocalPrefs provides prefs) {
        SimpleCRMTheme(accent = store.profile.accentColor, forceDark = prefs.darkModeOverride) {
            MainScaffold(store)
        }
    }
}

@Composable
private fun MainScaffold(store: CRMStore) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    var selectedTab by remember { mutableStateOf("dashboard") }
    if (route in tabs.map { it.route }) selectedTab = route!!

    val back: () -> Unit = { nav.popBackStack() }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val badge = when (tab.route) {
                        "tasks" -> store.openTasksCount
                        "more" -> store.unreadNotificationsCount
                        else -> 0
                    }
                    NavigationBarItem(
                        selected = selectedTab == tab.route,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(badge = { if (badge > 0) Badge { Text("$badge") } }) { Icon(tab.icon, tab.label) }
                        },
                        label = { Text(tab.label, maxLines = 1, softWrap = false, fontSize = 11.sp) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "dashboard", modifier = Modifier.padding(PaddingValues(bottom = padding.calculateBottomPadding()))) {
            composable("dashboard") { DashboardScreen() }
            composable("contacts") { ContactsScreen(onOpen = { nav.navigate("contact/$it") }) }
            composable("deals") { DealsScreen(onOpen = { nav.navigate("deal/$it") }) }
            composable("tasks") { TasksScreen() }
            composable("more") { MoreScreen { nav.navigate(it) } }

            composable("contact/{id}", listOf(navArgument("id") { type = NavType.StringType })) {
                ContactDetailScreen(it.arguments!!.getString("id")!!, back, onOpenDeal = { id -> nav.navigate("deal/$id") })
            }
            composable("deal/{id}", listOf(navArgument("id") { type = NavType.StringType })) {
                DealDetailScreen(it.arguments!!.getString("id")!!, back, onOpenContact = { id -> nav.navigate("contact/$id") })
            }
            composable("companies") { CompaniesScreen(back, onOpen = { nav.navigate("company/${android.net.Uri.encode(it)}") }) }
            composable("company/{name}", listOf(navArgument("name") { type = NavType.StringType })) {
                CompanyDetailScreen(it.arguments!!.getString("name")!!, back, onOpenContact = { id -> nav.navigate("contact/$id") })
            }
            composable("notes") { NotesScreen(back, onOpen = { nav.navigate("note/$it") }) }
            composable("note/{id}", listOf(navArgument("id") { type = NavType.StringType })) {
                NoteDetailScreen(it.arguments!!.getString("id")!!, back)
            }
            composable("calendar") { CalendarScreen(back) }
            composable("reports") { ReportsScreen(back) }
            composable("notifications") { NotificationsScreen(back) }
            composable("profile") { ProfileScreen(back) }
            composable("settings") { SettingsScreen(back, onNavigate = { nav.navigate(it) }) }
            composable("about") { AboutScreen(back) }
        }
    }
}
