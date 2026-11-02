package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = FastBiteSecondary,
  onPrimary = FastBiteOnSecondary,
  primaryContainer = FastBitePrimaryDark,
  secondary = FastBiteSecondary,
  tertiary = FastBiteTertiary,
  background = Color(0xFF19120E),
  surface = Color(0xFF241C18),
  surfaceVariant = Color(0xFF332822),
  onBackground = Color(0xFFEDE0DA),
  onSurface = Color(0xFFEDE0DA)
)

private val LightColorScheme = lightColorScheme(
  primary = FastBitePrimary,
  onPrimary = FastBiteOnPrimary,
  primaryContainer = FastBitePrimaryContainer,
  secondary = FastBiteSecondary,
  secondaryContainer = FastBiteSecondaryContainer,
  onSecondary = FastBiteOnSecondary,
  tertiary = FastBiteTertiary,
  tertiaryContainer = FastBiteTertiaryContainer,
  onTertiary = FastBiteOnTertiary,
  background = FastBiteBackground,
  surface = FastBiteSurface,
  surfaceVariant = FastBiteSurfaceVariant,
  onBackground = FastBiteOnSurface,
  onSurface = FastBiteOnSurface,
  outline = FastBiteOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent canteen branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
