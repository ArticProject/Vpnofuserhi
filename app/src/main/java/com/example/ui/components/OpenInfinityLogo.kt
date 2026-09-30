package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun OpenInfinityLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = w * 0.085f

        val path = Path().apply {
            val cx = w * 0.5f
            val cy = h * 0.5f
            val rx = w * 0.38f
            val ry = h * 0.22f

            moveTo(cx, cy)
            cubicTo(cx + rx * 0.5f, cy - ry, cx + rx, cy - ry, cx + rx, cy)
            cubicTo(cx + rx, cy + ry, cx + rx * 0.5f, cy + ry, cx, cy)
            cubicTo(cx - rx * 0.5f, cy - ry, cx - rx, cy - ry, cx - rx, cy)
            cubicTo(cx - rx, cy + ry, cx - rx * 0.5f, cy + ry, cx, cy)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}
