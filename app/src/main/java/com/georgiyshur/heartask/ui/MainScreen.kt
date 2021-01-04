package com.georgiyshur.heartask.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.georgiyshur.heartask.R
import com.georgiyshur.heartask.model.PlayerState
import com.georgiyshur.heartask.ui.artists.ArtistsScreen
import com.georgiyshur.heartask.ui.songs.ArtistSongsScreen
import com.georgiyshur.heartask.ui.theme.HearTheme
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel
import com.georgiyshur.heartask.viewmodel.PlayerViewModel
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun MainScreen(
    artistSongsViewModelFactory: ArtistSongsViewModel.AssistedFactory // Temporary workaround, see MainActivity for description
) {
    val navController = rememberNavController()
    val navigation = remember(navController) { Navigation(navController) }

    /*
     * TODO
     *
     * https://github.com/google/dagger/issues/2166
     *
     * Unfortunately at the moment (12/26/2020) composable-scoped Hilt injection isn't supported.
     * Moreover, the access to Hilt injection from the NavHost back stack entries doesn't work as
     * well because the Hilt ViewModel factory is missing in he NavBackStackEntry ViewModel store.
     * Although scoping ViewModels to composables should be supported in the future so it should
     * be enough to move this to its scope later.
     */
    val artistsViewModel: ArtistsViewModel = viewModel()
    val playerViewModel: PlayerViewModel = viewModel()

    HearTheme {
        ConstraintLayout(modifier = Modifier.fillMaxHeight()) {
            val (content, playerSheet) = createRefs()

            Box(modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top)
                bottom.linkTo(playerSheet.top)
                height = Dimension.fillToConstraints
            }) {
                NavHost(
                    navController = navController,
                    startDestination = Navigation.ARTISTS
                ) {
                    composable(Navigation.ARTISTS) {
                        ArtistsScreen(
                            onArtistClick = navigation.toSongs,
                            artistsViewModel = artistsViewModel
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
                            viewModelFactory = artistSongsViewModelFactory,
                            playerViewModel = playerViewModel
                        )
                    }
                }
            }
            PlayerBottomSheet(
                modifier = Modifier.constrainAs(playerSheet) {
                    bottom.linkTo(parent.bottom)
                    height = Dimension.wrapContent
                },
                playerViewModel = playerViewModel
            )
        }
    }
}

@Composable
private fun PlayerBottomSheet(
    modifier: Modifier,
    playerViewModel: PlayerViewModel
) {
    val playerState by playerViewModel.playerStateLiveData.observeAsState(PlayerState.Idle)

    if (playerState is PlayerState.Active) {
        val song = (playerState as PlayerState.Active).song
        val isPlaying = (playerState as PlayerState.Active).isPlaying
        val progress = (playerState as PlayerState.Active).progress
        Surface(
            modifier = modifier,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primaryVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = song.title,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = song.artist.username,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        PlayerButton(
                            drawableResId = R.drawable.ic_fast_rewind,
                            onClick = {
                                playerViewModel.rewind()
                            }
                        )
                        PlayerButton(
                            drawableResId = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                            onClick = {
                                if (isPlaying) {
                                    playerViewModel.pause()
                                } else {
                                    playerViewModel.play(song)
                                }
                            }
                        )
                        PlayerButton(
                            drawableResId = R.drawable.ic_fast_forward,
                            onClick = {
                                playerViewModel.forward()
                            }
                        )
                    }
                }
                val overlayColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.5f)
                // TODO make the timeline touchable/draggable
                CoilImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.5f))
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = overlayColor,
                                size = this.size.copy(width = this.size.width * progress)
                            )
                        },
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter(
                        MaterialTheme.colors.primary,
                        BlendMode.Xor
                    ), // invert colors
                    data = song.waveformUrl ?: ""
                )
            }
        }
    }
}

@Composable
private fun PlayerButton(
    size: Dp = 40.dp,
    drawableResId: Int,
    onClick: () -> Unit
) {
    Icon(
        imageVector = vectorResource(drawableResId).copy(defaultWidth = size, defaultHeight = size),
        modifier = Modifier
            .clickable(indication = rememberRipple(bounded = false)) { onClick() }
            .padding(12.dp),
        tint = MaterialTheme.colors.onPrimary
    )
}
