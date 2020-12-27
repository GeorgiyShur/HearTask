package com.georgiyshur.heartask.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.georgiyshur.heartask.ui.artists.ArtistsScreen
import com.georgiyshur.heartask.ui.songs.ArtistSongsScreen
import com.georgiyshur.heartask.ui.theme.HearTheme
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel

@Composable
fun MainScreen(
    artistSongsViewModelFactory: ArtistSongsViewModel.AssistedFactory // Temporary workaround, see MainActivity for description
) {
    val navController = rememberNavController()
    val navigation = remember(navController) { Navigation(navController) }

    /*
     * TODO
     *
     * Unfortunately at the moment (12/26/2020) composable-scoped Hilt injection isn't supported.
     * Moreover, the access to Hilt injection from the NavHost back stack entries doesn't work as
     * well because the Hilt ViewModel factory is missing in he NavBackStackEntry ViewModel store.
     * Although scoping ViewModels to composables should be supported in the future so it should
     * be enough to move this to its scope later.
     */
    val artistsViewModel: ArtistsViewModel = viewModel()

    HearTheme {
        NavHost(navController = navController, startDestination = Navigation.ARTISTS) {
            composable(Navigation.ARTISTS) {
                ArtistsScreen(
                    onArtistClick = navigation.toSongs,
                    viewModel = artistsViewModel
                )
            }
            composable(
                "${Navigation.ARTISTS}/{${Navigation.ARG_ARTIST_ID}}",
                arguments = listOf(navArgument(Navigation.ARG_ARTIST_ID) {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                ArtistSongsScreen(
                    artistId = backStackEntry.arguments?.getString(Navigation.ARG_ARTIST_ID)!!, // Artist ID should be always provided
                    navigateBack = navigation.back,
                    viewModelFactory = artistSongsViewModelFactory
                )
            }
        }
    }
}