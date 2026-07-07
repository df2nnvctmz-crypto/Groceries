package com.example.ui.theme

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

data class AppPaddings(
    val outerScreen: Dp,
    val innerCard: Dp,
    val elementSpacer: Dp
)

data class AppTypographySizes(
    val titleLarge: TextUnit,
    val titleMedium: TextUnit,
    val bodyMedium: TextUnit,
    val bodySmall: TextUnit,
    val labelSmall: TextUnit
)

data class AppIconSizes(
    val largeIcon: Dp,
    val standardIcon: Dp,
    val smallIcon: Dp
)

val CompactPaddings = AppPaddings(
    outerScreen = 12.dp,
    innerCard = 12.dp,
    elementSpacer = 8.dp
)

val MediumPaddings = AppPaddings(
    outerScreen = 16.dp,
    innerCard = 16.dp,
    elementSpacer = 16.dp
)

val LargePaddings = AppPaddings(
    outerScreen = 24.dp,
    innerCard = 24.dp,
    elementSpacer = 24.dp
)

val CompactFontSizes = AppTypographySizes(
    titleLarge = 24.sp,
    titleMedium = 16.sp,
    bodyMedium = 12.sp,
    bodySmall = 10.sp,
    labelSmall = 8.sp
)

val MediumFontSizes = AppTypographySizes(
    titleLarge = 32.sp,
    titleMedium = 20.sp,
    bodyMedium = 14.sp,
    bodySmall = 12.sp,
    labelSmall = 10.sp
)

val LargeFontSizes = AppTypographySizes(
    titleLarge = 36.sp,
    titleMedium = 24.sp,
    bodyMedium = 16.sp,
    bodySmall = 14.sp,
    labelSmall = 11.sp
)

val CompactIconSizes = AppIconSizes(
    largeIcon = 24.dp,
    standardIcon = 18.dp,
    smallIcon = 12.dp
)

val MediumIconSizes = AppIconSizes(
    largeIcon = 32.dp,
    standardIcon = 24.dp,
    smallIcon = 16.dp
)

val LargeIconSizes = AppIconSizes(
    largeIcon = 40.dp,
    standardIcon = 28.dp,
    smallIcon = 18.dp
)

val LocalAppPaddings = staticCompositionLocalOf { MediumPaddings }
val LocalAppFontSizes = staticCompositionLocalOf { MediumFontSizes }
val LocalAppIconSizes = staticCompositionLocalOf { MediumIconSizes }

object AppTheme {
    val paddings: AppPaddings
        @Composable
        get() = LocalAppPaddings.current

    val fontSizes: AppTypographySizes
        @Composable
        get() = LocalAppFontSizes.current

    val iconSizes: AppIconSizes
        @Composable
        get() = LocalAppIconSizes.current
}

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Only using light theme to match screenshot precisely
    val colorScheme = LightColorScheme

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val paddingValues = when {
        screenWidth >= 600 -> LargePaddings
        else -> {
            val scale = (screenWidth / 380f).coerceIn(0.8f, 1.0f)
            AppPaddings(
                outerScreen = (16 * scale).dp,
                innerCard = (16 * scale).dp,
                elementSpacer = (16 * scale).dp
            )
        }
    }
    val fontValues = when {
        screenWidth >= 600 -> LargeFontSizes
        else -> {
            val scale = (screenWidth / 380f).coerceIn(0.8f, 1.0f)
            AppTypographySizes(
                titleLarge = (32 * scale).sp,
                titleMedium = (20 * scale).sp,
                bodyMedium = (14 * scale).sp,
                bodySmall = (12 * scale).sp,
                labelSmall = (10 * scale).sp
            )
        }
    }
    val iconValues = when {
        screenWidth >= 600 -> LargeIconSizes
        else -> {
            val scale = (screenWidth / 380f).coerceIn(0.8f, 1.0f)
            AppIconSizes(
                largeIcon = (32 * scale).dp,
                standardIcon = (24 * scale).dp,
                smallIcon = (16 * scale).dp
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    CompositionLocalProvider(
        LocalAppPaddings provides paddingValues,
        LocalAppFontSizes provides fontValues,
        LocalAppIconSizes provides iconValues
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

