package com.georgiyshur.heartask.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HearError(error: Throwable?) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = error?.stackTraceToString() ?: "Unknown error",
        style = MaterialTheme.typography.subtitle1
    )
    // TODO probably add retry or something similar
}