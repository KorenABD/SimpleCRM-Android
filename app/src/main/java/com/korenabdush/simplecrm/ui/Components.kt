package com.korenabdush.simplecrm.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.korenabdush.simplecrm.Deal
import com.korenabdush.simplecrm.DealStage
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

// ---------- formatting & intents ----------

fun formatCurrency(value: Double, code: String = "USD"): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance(code)
        maximumFractionDigits = 0
    }.format(value)

private val zone: ZoneId get() = ZoneId.systemDefault()
private val dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
private val timeFmt = DateTimeFormatter.ofPattern("h:mm a")

fun formatDate(ms: Long): String = Instant.ofEpochMilli(ms).atZone(zone).format(dateFmt)
fun formatTime(ms: Long): String = Instant.ofEpochMilli(ms).atZone(zone).format(timeFmt)
fun formatRelative(ms: Long): String =
    DateUtils.getRelativeTimeSpanString(ms, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()

fun Context.openUri(uri: String, action: String = Intent.ACTION_VIEW) {
    runCatching { startActivity(Intent(action, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

fun Context.shareText(text: String) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

// ---------- screen chrome ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                    }
                },
                actions = actions,
            )
        },
        content = content,
    )
}

@Composable
fun EmptyState(title: String, message: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (message != null) Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun SectionCard(title: String? = null, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (title != null) Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
fun LabeledRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(checked, onChange)
    }
}

@Composable
fun StepperRow(label: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        FilledTonalIconButton(onMinus) { Icon(Icons.Default.Remove, "Decrease") }
        Spacer(Modifier.width(8.dp))
        FilledTonalIconButton(onPlus) { Icon(Icons.Default.Add, "Increase") }
    }
}

@Composable
fun ActionTile(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.12f)).clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, tint = accent)
        Text(label, style = MaterialTheme.typography.labelMedium, color = accent)
    }
}

@Composable
fun InitialsAvatar(text: String, diameter: Dp = 44.dp) {
    val palette = listOf(Color(0xFF2F6FED), Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFFFF9500), Color(0xFF009688), Color(0xFF3F51B5))
    val color = palette[text.sumOf { it.code } % palette.size]
    Box(Modifier.size(diameter).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = (diameter.value * 0.4f).sp)
    }
}

@Composable
fun PillBadge(text: String, color: Color) {
    Text(
        text,
        Modifier.clip(CircleShape).background(color.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp),
        color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = tint)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SimpleBarChart(values: List<Pair<String, Double>>) {
    val max = values.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0
    Row(Modifier.fillMaxWidth().height(130.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
        values.forEach { (label, v) ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.fillMaxWidth().height(maxOf(6.0, v / max * 100).dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.primary))
                Text(label, fontSize = 10.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DealRow(deal: Deal, company: String?, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(deal.title, fontWeight = FontWeight.Medium)
            if (company != null) Text(company, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(formatCurrency(deal.value), fontWeight = FontWeight.SemiBold)
            PillBadge(deal.stage.label, deal.stage.color)
        }
    }
}

// ---------- embedded web panel (the native equivalent of an iframe) ----------

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EmbeddedWebPanel(title: String, url: String, modifier: Modifier = Modifier) {
    var loading by remember { mutableStateOf(true) }
    val target = url
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(
            Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) { loading = false }
                            }
                            loadUrl(target)
                        }
                    },
                    update = { if (it.url != target && it.originalUrl != target) { loading = true; it.loadUrl(target) } },
                )
                if (loading) CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

fun osmEmbedUrl(lat: Double, lon: Double, span: Double = 0.25): String =
    "https://www.openstreetmap.org/export/embed.html?bbox=${lon - span},${lat - span},${lon + span},${lat + span}&layer=mapnik&marker=$lat,$lon"

// ---------- forms ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSheet(
    title: String,
    saveEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()).navigationBarsPadding().imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onDismiss) { Text("Cancel") }
                Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                TextButton(onSave, enabled = saveEnabled) { Text("Save") }
            }
            content()
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun Field(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier, minLines: Int = 1, singleLine: Boolean = minLines == 1) {
    OutlinedTextField(value, onChange, modifier.fillMaxWidth(), label = { Text(label) }, minLines = minLines, singleLine = singleLine)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PickerField(label: String, options: List<T>, selected: T, text: (T) -> String, onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(
            value = text(selected), onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(text(option)) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Segmented(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        options.forEachIndexed { i, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(i, options.size),
                label = { Text(label(option), maxLines = 1, fontSize = 11.sp) },
                icon = {},
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeField(label: String, millis: Long, withTime: Boolean, onChange: (Long) -> Unit) {
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var pending by remember { mutableLongStateOf(millis) }
    val text = if (withTime) "${formatDate(millis)}, ${formatTime(millis)}" else formatDate(millis)

    Row(Modifier.fillMaxWidth().clickable { showDate = true }.padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(text, color = MaterialTheme.colorScheme.primary)
    }

    if (showDate) {
        val localDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        val state = rememberDatePickerState(initialSelectedDateMillis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    showDate = false
                    state.selectedDateMillis?.let { sel ->
                        val picked = Instant.ofEpochMilli(sel).atZone(ZoneOffset.UTC).toLocalDate()
                        val time = Instant.ofEpochMilli(millis).atZone(zone).toLocalTime()
                        val combined = picked.atTime(time).atZone(zone).toInstant().toEpochMilli()
                        if (withTime) { pending = combined; showTime = true } else onChange(combined)
                    }
                }) { Text("OK") }
            },
            dismissButton = { TextButton({ showDate = false }) { Text("Cancel") } },
        ) { DatePicker(state) }
    }

    if (showTime) {
        val z = Instant.ofEpochMilli(pending).atZone(zone)
        val state = rememberTimePickerState(z.hour, z.minute, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    onChange(z.toLocalDate().atTime(state.hour, state.minute).atZone(zone).toInstant().toEpochMilli())
                    showTime = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton({ showTime = false }) { Text("Cancel") } },
            text = { TimePicker(state) },
        )
    }
}

@Composable
fun ConfirmDialog(title: String, message: String? = null, confirmLabel: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = message?.let { { Text(it) } },
        confirmButton = { TextButton({ onConfirm(); onDismiss() }) { Text(confirmLabel, color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun InfoDialog(title: String, message: String? = null, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = message?.let { { Text(it) } },
        confirmButton = { TextButton(onDismiss) { Text("OK") } },
    )
}

// ---------- swipe actions ----------

class LeadingAction(val label: String, val icon: ImageVector, val color: Color, val onAction: () -> Unit)

/** Swipe right for [leading], swipe left to delete. Neither swipe dismisses the row; callers handle the result. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeRow(onDelete: () -> Unit, leading: LeadingAction? = null, content: @Composable () -> Unit) {
    val state = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        when (value) {
            SwipeToDismissBoxValue.EndToStart -> onDelete()
            SwipeToDismissBoxValue.StartToEnd -> leading?.onAction()
            else -> {}
        }
        false
    })
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = leading != null,
        backgroundContent = {
            val toStart = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
            val bg = if (toStart) MaterialTheme.colorScheme.error else leading?.color ?: Color.Transparent
            Box(
                Modifier.fillMaxSize().background(bg).padding(horizontal = 20.dp),
                contentAlignment = if (toStart) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                val icon = if (toStart) Icons.Default.Delete else leading?.icon
                if (icon != null) Icon(icon, null, tint = Color.White)
            }
        },
    ) {
        Surface(color = MaterialTheme.colorScheme.background) { content() }
    }
}
