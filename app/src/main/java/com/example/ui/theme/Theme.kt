package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricBabyBlue,
    onPrimary = DarkGrayBackground,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueLight,
    secondary = ElectricBlueLight,
    onSecondary = DarkGrayBackground,
    tertiary = ElectricCyan,
    onTertiary = DarkGrayBackground,
    background = DarkGrayBackground,
    onBackground = TextPrimaryDark,
    surface = DarkGraySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkGrayCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkGrayBorder,
    outlineVariant = DarkGrayOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricBlueDark,
    onPrimary = Color.White,
    primaryContainer = ElectricBlueLight.copy(alpha = 0.3f),
    onPrimaryContainer = ElectricBlueDark,
    secondary = ElectricBlueDark,
    onSecondary = Color.White,
    tertiary = ElectricBabyBlue,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = LightOutline,
    outlineVariant = Color(0xFFE2E8F0)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Dynamic color disabled by default to maintain brand consistency
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
