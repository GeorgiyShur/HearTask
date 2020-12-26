package com.georgiyshur.heartask.ui.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import coil.transform.CircleCropTransformation
import com.georgiyshur.heartask.R
import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel
import com.georgiyshur.heartask.viewmodel.DataState
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.artists_title),
                        style = MaterialTheme.typography.h6
                    )
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        bodyContent = {
            ArtistsList(viewModel)
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun ArtistsList(viewModel: ArtistsViewModel) {
    val artistsDataState by viewModel.artistsLiveData.observeAsState()

    when (artistsDataState) {
        DataState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is DataState.Loaded -> {
            val artists = (artistsDataState as DataState.Loaded<List<Artist>>).data
            LazyColumn {
                items(artists) {
                    ArtistListItem(it)
                }
            }
        }
        is DataState.Error -> {
            val error = (artistsDataState as DataState.Error).error
            Text(
                modifier = Modifier.padding(16.dp),
                text = error?.stackTraceToString() ?: "Unknown error",
                style = MaterialTheme.typography.subtitle1
            )
            // TODO probably add retry or something similar
        }
    }
}

@Composable
private fun ArtistListItem(artist: Artist) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    // TODO go to songs
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