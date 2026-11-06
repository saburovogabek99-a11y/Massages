package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF1E3A8A),
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = AccentGreen,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF14532D),
  onSecondaryContainer = Color(0xFFDCFCE7),
  background = BgDark,            // #0F172A
  onBackground = TextPrimaryDark, // #F8FAFC
  surface = CardDark,             // #1E293B
  onSurface = TextPrimaryDark,
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = TextSecondaryDark,
  outline = BorderDark,
  error = ErrorRed,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,          // #2563EB
  onPrimary = Color.White,
  primaryContainer = PrimaryBlueContainer,
  onPrimaryContainer = PrimaryBlueDark,
  secondary = AccentGreen,        // #22C55E
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDCFCE7),
  onSecondaryContainer = Color(0xFF166534),
  background = BgLight,           // #F8FAFC
  onBackground = TextPrimaryLight,// #0F172A
  surface = CardLight,            // #FFFFFF
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderLight,
  error = ErrorRed,
  onError = Color.White
)

@Composable
fun MessengerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = MessengerTypography,
    content = content
  )
}
