package com.georgiyshur.heartask.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White

private val LightColors = lightColors(
    primary = BlueGrey500,
    primaryVariant = BlueGrey500,
    secondary = Lime500,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black
)

private val DarkColors = darkColors(
    primary = BlueGrey500,
    primaryVariant = BlueGrey500,
    secondary = Lime500,
    background = BlueGrey500,
    surface = BlueGrey700,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

@Composable
fun HearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}