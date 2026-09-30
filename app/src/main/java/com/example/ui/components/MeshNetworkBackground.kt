package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

private data class BaseNode(
    val baseX: Float,
    val baseY: Float
)

@Composable
fun MeshNetworkBackground(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    isConnected: Boolean = false
) {
    val baseNodes = remember {
        listOf(
            BaseNode(0.46f, 0.05f),
            BaseNode(0.65f, 0.07f),
            BaseNode(0.88f, 0.06f),
            BaseNode(0.76f, 0.12f),
            BaseNode(0.58f, 0.18f),
            BaseNode(0.04f, 0.32f),
            BaseNode(0.30f, 0.34f),
            BaseNode(0.82f, 0.30f),
            BaseNode(0.95f, 0.33f),
            BaseNode(0.22f, 0.44f),
            BaseNode(0.66f, 0.46f),
            BaseNode(0.18f, 0.56f),
            BaseNode(0.44f, 0.58f),
            BaseNode(0.78f, 0.55f),
            BaseNode(0.88f, 0.64f),
            BaseNode(0.65f, 0.72f),
            BaseNode(0.79f, 0.82f),
            BaseNode(0.94f, 0.80f),
            BaseNode(0.50f, 0.92f)
        )
    }

    val edges = remember {
        listOf(
            Pair(0, 1), Pair(1, 2), Pair(1, 3), Pair(0, 4), Pair(3, 4),
            Pair(5, 6), Pair(6, 9), Pair(9, 11), Pair(7, 8), Pair(7, 10),
            Pair(10, 12), Pair(11, 12), Pair(10, 13), Pair(13, 14),
            Pair(12, 15), Pair(13, 15), Pair(15, 16), Pair(16, 17), Pair(15, 18)
        )
    }

    val lineColor = if (isDarkTheme) {
        if (isConnected) Color(0xFF10B981).copy(alpha = 0.35f)
        else Color.White.copy(alpha = 0.12f)
    } else {
        if (isConnected) Color(0xFF10B981).copy(alpha = 0.30f)
        else Color(0xFF09090B).copy(alpha = 0.08f)
    }

    val dotColor = if (isDarkTheme) {
        if (isConnected) Color(0xFF10B981).copy(alpha = 0.75f)
        else Color.White.copy(alpha = 0.35f)
    } else {
        if (isConnected) Color(0xFF10B981).copy(alpha = 0.70f)
        else Color(0xFF09090B).copy(alpha = 0.18f)
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (w > 0f && h > 0f) {
            val points = baseNodes.map { node ->
                Offset(node.baseX * w, node.baseY * h)
            }

            for (edge in edges) {
                if (edge.first in points.indices && edge.second in points.indices) {
                    drawLine(
                        color = lineColor,
                        start = points[edge.first],
                        end = points[edge.second],
                        strokeWidth = 1.4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            for (pt in points) {
                drawCircle(
                    color = dotColor.copy(alpha = dotColor.alpha * 0.35f),
                    radius = 5.5f,
                    center = pt
                )
                drawCircle(
                    color = dotColor,
                    radius = 3.2f,
                    center = pt
                )
            }
        }
    }
}
