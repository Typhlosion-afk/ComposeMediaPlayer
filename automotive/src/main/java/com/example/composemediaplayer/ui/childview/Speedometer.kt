package com.example.composemediaplayer.ui.childview

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun Speedometer(
    modifier: Modifier = Modifier,
    currentSpeed: Float,
    minSpeed: Float = 0f,
    maxSpeed: Float = 180f,
    tickStep: Float = 20f,
) {
    // Animate the speed value for a smooth needle transition
    val animatedSpeed by animateFloatAsState(
        targetValue = currentSpeed,
        animationSpec = tween(durationMillis = 1000),
        label = "SpeedAnimation"
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = min(canvasWidth, canvasHeight) / 2.2f
        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

        val speedTicks = generateSequence(minSpeed) { it + tickStep }.takeWhile { it <= maxSpeed }

        // Draw Gradient Background Circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4A4A4A), Color(0xFF2C2C2C)),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        val sweepAngle = 270f
        val startAngle = 135f

        // Draw Tick Marks
        for (speed in speedTicks) {
            val angle = startAngle + (speed - minSpeed) / (maxSpeed - minSpeed) * sweepAngle
            val radian = Math.toRadians(angle.toDouble())

            val lineStart = Offset(
                x = center.x + (radius - 15.dp.toPx()) * cos(radian).toFloat(),
                y = center.y + (radius - 15.dp.toPx()) * sin(radian).toFloat()
            )
            val lineEnd = Offset(
                x = center.x + radius * cos(radian).toFloat(),
                y = center.y + radius * sin(radian).toFloat()
            )

            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = lineStart,
                end = lineEnd,
                strokeWidth = 3.dp.toPx()
            )

            // Draw Text Labels
            val label = speed.toInt().toString()
            val textRadius = radius - 35.dp.toPx()
            val textX = center.x + textRadius * cos(radian).toFloat()
            val textY = center.y + textRadius * sin(radian).toFloat()

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    label,
                    textX,
                    textY,
                    Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 14.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        alpha = 200
                    }
                )
            }
        }

        // Draw Needle (with shadow for depth)
        val clampedSpeed = animatedSpeed.coerceIn(minSpeed, maxSpeed)
        val needleAngle = startAngle + (clampedSpeed - minSpeed) / (maxSpeed - minSpeed) * sweepAngle
        val needleRadian = Math.toRadians(needleAngle.toDouble())
        val needleLength = radius - 20.dp.toPx()

        val needleEnd = Offset(
            x = center.x + needleLength * cos(needleRadian).toFloat(),
            y = center.y + needleLength * sin(needleRadian).toFloat()
        )
        val shadowOffset = Offset(4f, 4f)

        // Draw shadow
        drawLine(
            color = Color.Black.copy(alpha = 0.5f),
            start = center + shadowOffset,
            end = needleEnd + shadowOffset,
            strokeWidth = 8.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Draw main needle
        drawLine(
            color = Color.Red,
            start = center,
            end = needleEnd,
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center Hub
        drawCircle(
            color = Color.Red,
            radius = 12.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = center
        )
    }
}
