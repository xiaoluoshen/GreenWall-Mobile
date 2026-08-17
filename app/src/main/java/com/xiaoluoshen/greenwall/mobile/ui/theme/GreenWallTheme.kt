package com.xiaoluoshen.greenwall.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

private val GreenLightScheme = lightColorScheme(
    primary = Color(0xFF146C2E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA2F5AB),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF4F6350),
    surface = Color(0xFFFCFDF7),
    surfaceVariant = Color(0xFFDEE5D8),
    background = Color(0xFFFCFDF7),
    error = Color(0xFFBA1A1A),
)

private val GreenDarkScheme = darkColorScheme(
    primary = Color(0xFF86D990),
    onPrimary = Color(0xFF003916),
    primaryContainer = Color(0xFF005321),
    onPrimaryContainer = Color(0xFFA2F5AB),
    secondary = Color(0xFFB7CCB7),
    surface = Color(0xFF101510),
    surfaceVariant = Color(0xFF424940),
    background = Color(0xFF101510),
    error = Color(0xFFFFB4AB),
)

@Composable
fun GreenWallTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) GreenDarkScheme else GreenLightScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
