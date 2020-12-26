package com.georgiyshur.heartask.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.georgiyshur.heartask.ui.artists.ArtistsScreen
import com.georgiyshur.heartask.ui.theme.HearTheme
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navigation = remember(navController) { Navigation(navController) }

    /*
     * TODO
     *
     * Unfortunately at the moment (12/26/2020) composable-scoped Hilt injection isn't supported.
     * Moreover, the access to Hilt injection from the NavHost back stack entries doesn't work as
     * well because the Hilt ViewModel factory is missing in he NavBackStackEntry ViewModel store.
     * Although scoping ViewModels to composables should be supported in the future so it should
     * be enough to remove this later.
     */
    val artistsViewModel: ArtistsViewModel = viewModel()

    HearTheme {
        NavHost(navController = navController, startDestination = Navigation.Artists) {
            composable(Navigation.Artists) {
                ArtistsScreen(artistsViewModel)
            }
        }
    }
}