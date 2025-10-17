package com.example.composemediaplayer.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composemediaplayer.R
import com.example.composemediaplayer.core.toTimeString
import com.example.composemediaplayer.data.Song
import com.example.composemediaplayer.ui.MainViewModel

@Composable
fun NowPlayingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle(initialValue = 0L)

    var isSplitScreen by remember { mutableStateOf(false) }

    val nowPlayingWeight by animateFloatAsState(
        targetValue = if (isSplitScreen) 0.35f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "NowPlayingWeight"
    )

    val songListWeight by animateFloatAsState(
        targetValue = if (isSplitScreen) 0.65f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "SongListWeight"
    )

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(nowPlayingWeight)
        ) {
            NowPlayingContent(
                song = currentSong,
                isPlaying = isPlaying,
                playbackPosition = playbackPosition,
                isSplitScreen = isSplitScreen,
                onBack = onBack,
                onPlayPause = {
                    if (isPlaying) viewModel.pause() else viewModel.resume()
                },
                onNext = { viewModel.next() },
                onPrev = { viewModel.previous() },
                onSeek = { position -> viewModel.seekTo(position) },
                onShowList = { isSplitScreen = !isSplitScreen }
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxHeight()
                .weight(songListWeight),
            visible = isSplitScreen,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500)
            ) + fadeIn(tween(500)),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500)
            ) + fadeOut(tween(500))
        ) {
            SongListScreen(
                viewModel = viewModel,
                onSongClick = { song ->
                    viewModel.playSong(song)
                }
            )
        }
    }
}
@Composable
fun NowPlayingContent(
    song: Song?,
    isPlaying: Boolean,
    playbackPosition: Long,
    isSplitScreen: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Long) -> Unit,
    onShowList: () -> Unit
) {
    song?.let {
        val totalDuration = song.duration
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        var isSeeking by remember { mutableStateOf(false) }

        // Animate the artwork size
        val artworkSize by animateDpAsState(
            targetValue = if (isSplitScreen) 120.dp else 200.dp,
            animationSpec = tween(500),
            label = "artworkSize"
        )

        // Animate font sizes
        val titleFontSize by animateFloatAsState(
            targetValue = if (isSplitScreen) 22f else 32f,
            animationSpec = tween(500),
            label = "titleFontSize"
        )
        val artistFontSize by animateFloatAsState(
            targetValue = if (isSplitScreen) 16f else 20f,
            animationSpec = tween(500),
            label = "artistFontSize"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderRow(onBack = onBack, onShowList = onShowList)

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_bg_playlist_default),
                    contentDescription = null,
                    modifier = Modifier
                        .size(artworkSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = song.title,
                    fontSize = titleFontSize.sp,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorResource(id = R.color.text_primary),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = song.artist,
                    fontSize = artistFontSize.sp, // Use animated font size
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            LaunchedEffect(playbackPosition, isSeeking) {
                if (!isSeeking) {
                    sliderPosition = playbackPosition.toFloat()
                }
            }

            // --- START OF CHANGES ---

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Add space between elements
            ) {
                // Current Position Text
                Text(
                    text = playbackPosition.toTimeString(),
                    style = MaterialTheme.typography.bodyMedium, // Make font slightly bigger
                    color = colorResource(id = R.color.text_secondary)
                )

                // Slider
                Slider(
                    value = sliderPosition,
                    onValueChange = { newProgress ->
                        isSeeking = true
                        sliderPosition = newProgress
                    },
                    onValueChangeFinished = {
                        onSeek(sliderPosition.toLong())
                        isSeeking = false
                    },
                    valueRange = 0f..totalDuration.toFloat(),
                    modifier = Modifier.weight(1f) // Let the slider take the remaining space
                )

                // Total Duration Text
                Text(
                    text = totalDuration.toTimeString(),
                    style = MaterialTheme.typography.bodyMedium, // Make font slightly bigger
                    color = colorResource(id = R.color.text_secondary)
                )
            }

            // --- END OF CHANGES ---


            Spacer(modifier = Modifier.height(16.dp))

            // Playback controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ... (Icon buttons remain the same)
// Previous Button
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = "Previous",
                    tint = colorResource(id = R.color.text_primary),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = onPrev
                        )
                )

                // Play/Pause Button
                Icon(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = colorResource(id = R.color.text_primary),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = onPlayPause
                        )
                )

                // Next Button
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = "Next",
                    tint = colorResource(id = R.color.text_primary),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = onNext
                        )
                )
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No song selected", modifier = Modifier.padding(16.dp))
        }
    }
}
@Composable
fun HeaderRow(onBack: () -> Unit, onShowList: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back",
                tint = colorResource(id = R.color.text_primary),
            )
        }
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.text_primary),
            fontSize = 32.sp,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_show_list),
            contentDescription = "Show List",
            tint = colorResource(id = R.color.text_primary),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 24.dp), // Control ripple size
                onClick = onShowList
            )
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 480
)
@Composable
fun NowPlayingContentFullScreenPreview() {
    NowPlayingContent(
        song = Song(
            id = "1",
            title = "A Cool Song Title",
            artist = "A Famous Artist",
            album = "An Awesome Album",
            duration = 240000L
        ),
        isPlaying = true,
        playbackPosition = 60000L,
        isSplitScreen = false,
        onBack = {},
        onPlayPause = {},
        onNext = {},
        onPrev = {},
        onSeek = {},
        onShowList = {}
    )
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 480
)
@Composable
fun NowPlayingContentSplitScreenPreview() {
    Row {
        Box(modifier = Modifier.weight(0.35f)) {
            NowPlayingContent(
                song = Song(
                    id = "1",
                    title = "A Cool Song Title",
                    artist = "A Famous Artist",
                    album = "An Awesome Album",
                    duration = 240000L // 4 minutes
                ),
                isPlaying = false,
                playbackPosition = 120000L, // 2 minutes in
                isSplitScreen = true,
                onBack = {},
                onPlayPause = {},
                onNext = {},
                onPrev = {},
                onSeek = {},
                onShowList = {}
            )
        }
        Box(modifier = Modifier.weight(0.65f)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Song List would appear here")
            }
        }
    }
}