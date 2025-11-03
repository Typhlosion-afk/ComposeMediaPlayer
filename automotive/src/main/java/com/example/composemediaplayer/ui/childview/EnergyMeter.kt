package com.example.composemediaplayer.ui.childview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnergyMeter(
    modifier: Modifier = Modifier,
    currentLevel: Float,
    isDarkMode: Boolean = false,
    minLevel: Float = 0f,
    maxLevel: Float = 100f,
    unitLabel: String = "%"
) {
    val clampedLevel = currentLevel.coerceIn(minLevel, maxLevel)
    val percentage = (clampedLevel - minLevel) / (maxLevel - minLevel)

    // Animate the arc sweep angle for a smooth progress change
    val animatedSweepAngle by animateFloatAsState(
        targetValue = 270f * percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "EnergySweepAnimation"
    )

    // === Color Logic ===
    // Define the different color sets for the gradient
    val normalColors = listOf(Color(0xFF3887FE), Color(0xFF56F1B3)) // Blue to Green
    val warningColors = listOf(Color(0xFFD32F2F), Color(0xFFFFC107)) // Red to Yellow
    val criticalColor = Color(0xFFD32F2F) // Solid Red

    // Determine which brush to use based on the current energy level
    val progressBrush = when {
        clampedLevel < 20 -> Brush.sweepGradient(listOf(criticalColor, criticalColor))
        clampedLevel < 40 -> Brush.sweepGradient(warningColors)
        else -> Brush.sweepGradient(normalColors)
    }

    // Animate the text color for a smooth transition to red
    val textColor by animateColorAsState(
        targetValue = if (!isDarkMode) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 300),
        label = "TextColorAnimation"
    )
    // ===================

    Box(
        modifier = modifier.aspectRatio(1f), // Ensure the meter is always a square
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 20.dp.toPx()
            val startAngle = 135f

            // 1. Draw the background track arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.3f),
                startAngle = startAngle,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Draw the progress arc with the dynamically selected gradient
            drawArc(
                brush = progressBrush, // Use the dynamic brush
                startAngle = startAngle,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // 3. Display the current level text in the center
        Text(
            text = "${clampedLevel.toInt()}$unitLabel",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = textColor // Use the animated text color
        )
    }
}
