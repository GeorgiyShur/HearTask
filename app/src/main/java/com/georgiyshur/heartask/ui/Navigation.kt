package com.georgiyshur.heartask.ui

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate

/**
 * Class responsible for the navigation of the app. It contains destination names and routes
 * between them represented as functions.
 */
class Navigation(navController: NavHostController) {

    companion object {
        const val Artists = "artists"
    }

    val toSongs: (Int) -> Unit = { artistId ->
        navController.navigate("$Artists/$artistId")
    }
}