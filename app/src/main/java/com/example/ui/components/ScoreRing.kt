package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScoreRingAmber
import com.example.ui.theme.ScoreRingBackground
import com.example.ui.theme.ScoreRingGreen
import com.example.ui.theme.ScoreRingRed

import com.example.ui.theme.AppTheme

@Composable
fun ScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.iconSizes.largeIcon * 2,
    strokeWidth: Dp = AppTheme.iconSizes.smallIcon * 0.4f,
    textSize: TextUnit = AppTheme.fontSizes.titleMedium
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) score / 100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "score_animation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    val color = when {
        score >= 80 -> ScoreRingGreen
        score >= 40 -> ScoreRingAmber
        else -> ScoreRingRed
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Background track
            drawArc(
                color = ScoreRingBackground,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage.value,
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = score.toString(),
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
