package com.xiaoluoshen.greenwall.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

private val GreenLightScheme = lightColorScheme(
    primary = Color(0xFF146C2E),
    onPrimary = Color.White,
    primaryVariant = Color(0xFFE0F3E2),
    onPrimaryVariant = Color(0xFF123A1B),
    primaryContainer = Color(0xFF2D7C43),
    onPrimaryContainer = Color.White,
    tertiaryContainer = Color(0xFFE0F3E2),
    onTertiaryContainer = Color(0xFF146C2E),
    background = Color(0xFFFCFDF7),
    surface = Color(0xFFF5F7F2),
    surfaceVariant = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFEDF1EA),
    surfaceContainerHighest = Color(0xFFE6EBE3),
)

private val GreenDarkScheme = darkColorScheme(
    primary = Color(0xFF86D990),
    onPrimary = Color(0xFF003916),
    primaryVariant = Color(0xFF005321),
    onPrimaryVariant = Color(0xFFA2F5AB),
    primaryContainer = Color(0xFF3A914F),
    onPrimaryContainer = Color(0xFF001B08),
    tertiaryContainer = Color(0xFF173C20),
    onTertiaryContainer = Color(0xFF9BE9A8),
    background = Color(0xFF101510),
    surface = Color(0xFF171C17),
    surfaceVariant = Color(0xFF202720),
    surfaceContainer = Color(0xFF202720),
    surfaceContainerHigh = Color(0xFF2A332A),
    surfaceContainerHighest = Color(0xFF343E34),
)

@Composable
fun GreenWallTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) GreenDarkScheme else GreenLightScheme
    MiuixTheme(colors = colors, content = content)
}
