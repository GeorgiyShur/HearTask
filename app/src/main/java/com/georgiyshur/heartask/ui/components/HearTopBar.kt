package com.georgiyshur.heartask.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun HearTopBar(
    title: String,
    navigateBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )
        },
        navigationIcon = navigateBack?.let {
            {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Rounded.ArrowBack)
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary
    )
}