package com.example.composemediaplayer.ui.screen.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
                MapTextureView(context, bitmap)
            }
        )
    }
}

// ----------------------------
// CUSTOM SURFACE VIEW
// ----------------------------
@SuppressLint("ViewConstructor")
class MapTextureView(
    context: Context,
    private val bitmap: Bitmap?
) : TextureView(context), TextureView.SurfaceTextureListener {

    private val locationColor = android.graphics.Color.parseColor("#1E88E5")

    private val fillPaint = android.graphics.Paint().apply {
        color = locationColor
        style = android.graphics.Paint.Style.FILL
        isAntiAlias = true
    }

    private val strokePaint = android.graphics.Paint().apply {
        color = locationColor
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    // Animation state
    private var pulseRadius = 18f
    private var pulseAlpha = 255

    private var animator: android.animation.ValueAnimator? = null

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, w: Int, h: Int) {
        startPulse()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, w: Int, h: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        stopPulse()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    // ----------------------------
    // Animation
    // ----------------------------
    private fun startPulse() {
        animator?.cancel()

        animator = android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1200
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.RESTART

            addUpdateListener { valueAnimator ->
                val fraction = valueAnimator.animatedFraction

                pulseRadius = 18f + fraction * 22f      // 18 → 40
                pulseAlpha = (255 * (1f - fraction)).toInt()

                drawMap()
            }
            start()
        }
    }

    private fun stopPulse() {
        animator?.cancel()
        animator = null
    }

    // ----------------------------
    // Drawing
    // ----------------------------
    private fun drawMap() {
        val canvas = lockCanvas() ?: return

        canvas.drawColor(android.graphics.Color.WHITE)

        // Draw map
        bitmap?.let {
            val left = (width - it.width) / 2f
            val top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, null)
        }

        val cx = width / 2f
        val cy = height / 2f

        // Pulse stroke
        strokePaint.alpha = pulseAlpha
        canvas.drawCircle(cx, cy, pulseRadius, strokePaint)

        // Center dot
        canvas.drawCircle(cx, cy, 8f, fillPaint)

        unlockCanvasAndPost(canvas)
    }
}

@Composable
fun SearchingPanel(
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }

    val items = listOf("Hà Nội", "Đà Nẵng", "TP. Hồ Chí Minh", "Nha Trang")

    val filteredItems = items.filter {
        it.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search") },
            singleLine = true,
            shape = CircleShape
        )

        Spacer(modifier = Modifier.height(12.dp))

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems) { item ->
                Text(
                    text = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}