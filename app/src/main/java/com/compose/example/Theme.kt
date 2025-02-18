package com.compose.example

import android.app.Activity
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
import androidx.core.view.WindowInsetsControllerCompat

val primaryLight = Color(0xFF1D6586)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC4E7FF)
val onPrimaryContainerLight = Color(0xFF004C69)
val secondaryLight = Color(0xFF4E616D)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFD1E5F4)
val onSecondaryContainerLight = Color(0xFF374955)
val tertiaryLight = Color(0xFF615A7D)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFE6DEFF)
val onTertiaryContainerLight = Color(0xFF494264)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFF6FAFE)
val onBackgroundLight = Color(0xFF181C1F)
val surfaceLight = Color(0xFFF6FAFE)
val onSurfaceLight = Color(0xFF181C1F)
val surfaceVariantLight = Color(0xFFDDE3EA)
val onSurfaceVariantLight = Color(0xFF41484D)
val outlineLight = Color(0xFF71787E)
val outlineVariantLight = Color(0xFFC0C7CD)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2C3134)
val inverseOnSurfaceLight = Color(0xFFEDF1F5)
val inversePrimaryLight = Color(0xFF90CEF4)
val surfaceDimLight = Color(0xFFD7DADF)
val surfaceBrightLight = Color(0xFFF6FAFE)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF0F4F8)
val surfaceContainerLight = Color(0xFFEAEEF3)
val surfaceContainerHighLight = Color(0xFFE5E8ED)
val surfaceContainerHighestLight = Color(0xFFDFE3E7)

val primaryDark = Color(0xFF90CEF4)
val onPrimaryDark = Color(0xFF00344A)
val primaryContainerDark = Color(0xFF004C69)
val onPrimaryContainerDark = Color(0xFFC4E7FF)
val secondaryDark = Color(0xFFB5C9D7)
val onSecondaryDark = Color(0xFF20333E)
val secondaryContainerDark = Color(0xFF374955)
val onSecondaryContainerDark = Color(0xFFD1E5F4)
val tertiaryDark = Color(0xFFCAC1E9)
val onTertiaryDark = Color(0xFF322C4C)
val tertiaryContainerDark = Color(0xFF494264)
val onTertiaryContainerDark = Color(0xFFE6DEFF)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF0F1417)
val onBackgroundDark = Color(0xFFDFE3E7)
val surfaceDark = Color(0xFF0F1417)
val onSurfaceDark = Color(0xFFDFE3E7)
val surfaceVariantDark = Color(0xFF41484D)
val onSurfaceVariantDark = Color(0xFFC0C7CD)
val outlineDark = Color(0xFF8B9297)
val outlineVariantDark = Color(0xFF41484D)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFDFE3E7)
val inverseOnSurfaceDark = Color(0xFF2C3134)
val inversePrimaryDark = Color(0xFF1D6586)
val surfaceDimDark = Color(0xFF0F1417)
val surfaceBrightDark = Color(0xFF353A3D)
val surfaceContainerLowestDark = Color(0xFF0A0F12)
val surfaceContainerLowDark = Color(0xFF181C1F)
val surfaceContainerDark = Color(0xFF1C2023)
val surfaceContainerHighDark = Color(0xFF262B2E)
val surfaceContainerHighestDark = Color(0xFF313539)


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    val context = LocalContext.current
    val window = (context as Activity).window
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightStatusBars = darkTheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
