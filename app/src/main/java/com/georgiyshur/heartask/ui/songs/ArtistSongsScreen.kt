package com.georgiyshur.heartask.ui.songs

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.hours
import androidx.compose.ui.unit.inSeconds
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.ui.components.HearError
import com.georgiyshur.heartask.ui.components.HearProgressBar
import com.georgiyshur.heartask.ui.components.HearTopBar
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
import com.georgiyshur.heartask.viewmodel.DataState
import com.georgiyshur.heartask.viewmodel.PlayerViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun ArtistSongsScreen(
    artistId: String,
    navigateBack: () -> Unit,
    viewModelFactory: ArtistSongsViewModel.AssistedFactory, // Temporary workaround, see MainActivity for description
    playerViewModel: PlayerViewModel
) {
    val artistSongsViewModel = viewModelFactory.create(artistId)
    val artist by artistSongsViewModel.artistLiveData.observeAsState()

    Scaffold(
        topBar = {
            HearTopBar(
                title = artist!!.username, // Artist in non-null in data layer
                navigateBack = navigateBack,
            )
        },
        bodyContent = {
            SongsList(artistSongsViewModel, playerViewModel)
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
private fun SongsList(
    artistSongsViewModel: ArtistSongsViewModel,
    playerViewModel: PlayerViewModel
) {
    val songsDataState by artistSongsViewModel.songsLiveData.observeAsState()

    when (songsDataState) {
        DataState.Loading -> {
            HearProgressBar()
        }
        is DataState.Loaded -> {
            val songs = (songsDataState as DataState.Loaded<List<Song>>).data
            LazyColumn {
                itemsIndexed(songs) { index, song ->
                    SongListItem(song, playerViewModel)
                    if (index < songs.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
        is DataState.Error -> {
            HearError((songsDataState as DataState.Error).error)
        }
    }
}

@Composable
private fun SongListItem(
    song: Song,
    playerViewModel: PlayerViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    playerViewModel.play(song)
                }
            )
            .padding(16.dp)
    ) {
        SongImage(
            url = song.artworkUrl ?: "",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = song.artist.username,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = song.title,
                style = MaterialTheme.typography.body2
            )
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = buildString {
                        append(song.genre)
                        append(" • ")
                        append(
                            DateTimeFormatter.ofPattern(
                                if (song.duration < 1.hours.inSeconds()) {
                                    "mm:ss"
                                } else "hh:mm:ss"
                            ).withZone(ZoneId.of("UTC"))
                                .format(
                                    LocalDateTime.ofInstant(
                                        Instant.ofEpochSecond(song.duration),
                                        ZoneId.of("UTC")
                                    )
                                )
                        )
                    },
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }
}

@Composable
private fun SongImage(
    url: String,
    modifier: Modifier = Modifier
) {
    CoilImage(
        modifier = modifier.preferredSize(64.dp, 64.dp),
        data = url
    )
}