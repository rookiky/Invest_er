package com.example.invest.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Logo,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Black, // Set background to black
    surface = Black,    // Set surface to black
    onBackground = TextBlack, // Text color on black background
    onSurface = ButtonBlack,
)

private val LightColorScheme = lightColorScheme(
    primary = Like,
    secondary = Dislike,
    tertiary = White,
    background = Color.Black, // Set background to black
    surface = Color.Black,    // Set surface to black
    onBackground = Color.White, // Text color on black background
    onSurface = Color.White,
    onPrimary = Logo
    /* Other default colors to override
    ,
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun InvestTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,

    )
}