package com.georgiyshur.heartask.ui

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate

/**
 * Class responsible for the navigation of the app. It contains destination names and routes
 * between them represented as functions.
 */
class Navigation(navController: NavHostController) {

    companion object {
        const val ARTISTS = "artists"

        const val ARG_ARTIST_ID = "artistId"
    }

    val toSongs: (String) -> Unit = { artistId ->
        navController.navigate("$ARTISTS/$artistId")
    }

    val back: () -> Unit = {
        navController.popBackStack()
    }
}
