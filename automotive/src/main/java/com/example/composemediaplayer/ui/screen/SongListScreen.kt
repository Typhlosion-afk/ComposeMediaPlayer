package com.example.composemediaplayer.ui.screen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composemediaplayer.R
import com.example.composemediaplayer.core.toTimeString
import com.example.composemediaplayer.data.Song
import com.example.composemediaplayer.ui.MainViewModel

@Composable
fun SongListScreen(viewModel: MainViewModel, onSongClick: (Song) -> Unit) {
    val currentListSong by viewModel.currentListSong.collectAsStateWithLifecycle()
    val customShape = RoundedCornerShape(
        topStart = 200.dp,
        bottomStart = 200.dp,
        topEnd = 0.dp,
        bottomEnd = 0.dp
    )

    LazyColumn {
        items(currentListSong) { song ->
            Surface(
                shape = customShape,
                color = colorResource(id = R.color.card_background_color),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) {
                        onSongClick(song)
                    }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        colors = ListItemDefaults.colors(colorResource(id = R.color.card_background_color)),
                        headlineContent = {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    song.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 20.sp,
                                    color = colorResource(id = R.color.text_primary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    song.artist,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorResource(id = R.color.text_secondary),
                                    fontSize = 16.sp,
                                )
                            }
                        },
                        leadingContent = {
                            Image(
                                painter = painterResource(id = R.drawable.img_bg_playlist_default),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        },
                    )

                    Text(
                        text = song.duration.toTimeString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(id = R.color.text_secondary),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}