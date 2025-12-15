package com.example.smartirrigation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Green80,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002200),
    secondary = GreenGrey80,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F8DA),
    onSecondaryContainer = Color(0xFF002200),
    tertiary = Teal80,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF9FF8BBD0),
    onTertiaryContainer = Color(0xFF3E001D),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1A1C1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFE0E3E0),
    onSurfaceVariant = Color(0xFF424941),
    error = Color(0xFFB00020),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    onErrorContainer = Color(0xFF410E0B)
)

private val DarkColors = darkColorScheme(
    primary = Green40,
    onPrimary = Color(0xFF003910),
    primaryContainer = Teal40,
    onPrimaryContainer = Color(0xFFA1F5A1),
    secondary = GreenGrey40,
    onSecondary = Color(0xFF003914),
    secondaryContainer = Color(0xFF1F4D24),
    onSecondaryContainer = Color(0xB3C8E6C9),
    tertiary = GreenGrey40,
    onTertiary = Color(0xFF00391C),
    tertiaryContainer = Color(0xFF1A4D2B),
    onTertiaryContainer = Color(0xB1F1C8),
    background = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF424941),
    onSurfaceVariant = Color(0xFFC1C9BF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun SmartIrrigationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
