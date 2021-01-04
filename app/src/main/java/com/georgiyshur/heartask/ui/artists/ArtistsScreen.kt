package com.georgiyshur.heartask.ui.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.georgiyshur.heartask.R
import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.ui.components.HearError
import com.georgiyshur.heartask.ui.components.HearProgressBar
import com.georgiyshur.heartask.ui.components.HearTopBar
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel
import com.georgiyshur.heartask.viewmodel.DataState
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun ArtistsScreen(
    onArtistClick: (String) -> Unit,
    artistsViewModel: ArtistsViewModel
) {
    Scaffold(
        topBar = {
            HearTopBar(title = stringResource(id = R.string.artists_title))
        },
        bodyContent = {
            ArtistsList(
                onArtistClick = onArtistClick,
                viewModel = artistsViewModel
            )
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun ArtistsList(
    onArtistClick: (String) -> Unit,
    viewModel: ArtistsViewModel
) {
    val artistsDataState by viewModel.artistsLiveData.observeAsState()

    when (artistsDataState) {
        DataState.Loading -> {
            HearProgressBar()
        }
        is DataState.Loaded -> {
            val artists = (artistsDataState as DataState.Loaded<List<Artist>>).data
            LazyColumn {
                itemsIndexed(artists) { index, artist ->
                    ArtistListItem(
                        artist = artist,
                        onArtistClick = onArtistClick
                    )
                    if (index < artists.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
        is DataState.Error -> {
            HearError((artistsDataState as DataState.Error).error)
        }
    }
}

@Composable
private fun ArtistListItem(
    artist: Artist,
    onArtistClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onArtistClick(artist.id)
                }
            )
            .padding(16.dp)
    ) {
        ArtistImage(url = artist.avatarUrl ?: "")
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = artist.username,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ArtistImage(url: String) {
    CoilImage(
        modifier = Modifier.preferredSize(32.dp, 32.dp),
        data = url,
        requestBuilder = {
            transformations(CircleCropTransformation())
        }
    )
}