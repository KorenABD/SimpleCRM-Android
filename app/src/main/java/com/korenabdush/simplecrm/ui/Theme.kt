package com.korenabdush.simplecrm.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SimpleCRMTheme(accent: Color, forceDark: Boolean, content: @Composable () -> Unit) {
    val dark = forceDark || isSystemInDarkTheme()
    val scheme = if (dark) darkColorScheme(primary = accent) else lightColorScheme(primary = accent)
    MaterialTheme(colorScheme = scheme, content = content)
}
