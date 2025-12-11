package com.example.composemediaplayer.ui.screen.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composemediaplayer.R

// ----------------------------
// MAIN SCREEN
// ----------------------------
@Composable
fun NavigationScreen() {

    Row(Modifier.fillMaxSize()) {

        SearchingPanel(
            modifier = Modifier.weight(1f)
        )

        AndroidView(
            modifier = Modifier.weight(3f),
            factory = { context ->

                val bitmap = BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.mock_map
                )

                MapSurfaceView(context, bitmap)
            }
        )
    }
}

//
// ----------------------------
// CUSTOM SURFACE VIEW
// ----------------------------
//
@SuppressLint("ViewConstructor")
class MapSurfaceView(
    context: Context,
    private val bitmap: Bitmap?
) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        drawMap()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun drawMap() {
        val canvas = holder.lockCanvas() ?: return

        canvas.drawColor(android.graphics.Color.WHITE)

        bitmap?.let {
            val left = (width - it.width) / 2f
            val top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, null)
        }

        holder.unlockCanvasAndPost(canvas)
    }
}

@Composable
fun SearchingPanel(
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }

    val historyList = listOf("Hà Nội", "Đà Nẵng", "TP. Hồ Chí Minh", "Nha Trang")

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        // ---- SEARCH BAR ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search…") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

//            Icon(
//                imageVector = Icons.Default.Mic,
//                contentDescription = "Voice search",
//                modifier = Modifier
//                    .size(28.dp)
//                    .clickable { /* voice search click */ }
//            )
        }

        Spacer(Modifier.height(20.dp))

        Text("Search History")

        Spacer(Modifier.height(12.dp))

//         ---- SEARCH HISTORY LIST ----
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(historyList) { item ->
                Text(text = item, color = Color.Black)
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { /* handle click */ }
//                        .padding(vertical = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.History,
//                        contentDescription = null
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Text(item)
//                }
            }
        }
    }
}
