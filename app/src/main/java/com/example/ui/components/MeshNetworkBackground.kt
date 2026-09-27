package com.example.ui.components

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.sqrt
import kotlin.random.Random

private class MeshNode(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float
)

@Composable
fun MeshNetworkBackground(
    modifier: Modifier = Modifier,
    isConnected: Boolean = false
) {
    val nodes = remember {
        mutableStateListOf<MeshNode>().apply {
            val random = Random(42)
            repeat(16) {
                add(
                    MeshNode(
                        x = random.nextFloat(),
                        y = random.nextFloat(),
                        vx = (random.nextFloat() - 0.5f) * 0.0003f,
                        vy = (random.nextFloat() - 0.5f) * 0.0003f,
                        radius = 2.0f + random.nextFloat() * 1.5f
                    )
                )
            }
        }
    }

    var frameTick by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withInfiniteAnimationFrameMillis { millis ->
                frameTick = (millis % 100000L).toFloat()
                for (node in nodes) {
                    node.x += node.vx
                    node.y += node.vy
                    if (node.x < 0.02f || node.x > 0.98f) node.vx *= -1f
                    if (node.y < 0.02f || node.y > 0.98f) node.vy *= -1f
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Crisp pure white luxury background (exactly matching Vellor screenshot)
        drawRect(color = Color(0xFFFFFFFF))

        val maxDist = width * 0.44f
        val lineBaseColor = if (isConnected) Color(0xFF10B981) else Color(0xFF000000)
        val lineAlpha = if (isConnected) 0.12f else 0.07f

        for (i in 0 until nodes.size) {
            val n1 = nodes[i]
            val p1 = Offset(n1.x * width, n1.y * height)

            for (j in i + 1 until nodes.size) {
                val n2 = nodes[j]
                val p2 = Offset(n2.x * width, n2.y * height)

                val dx = p1.x - p2.x
                val dy = p1.y - p2.y
                val dist = sqrt(dx * dx + dy * dy)

                if (dist < maxDist) {
                    val strength = (1f - (dist / maxDist)) * lineAlpha
                    drawLine(
                        color = lineBaseColor.copy(alpha = strength),
                        start = p1,
                        end = p2,
                        strokeWidth = 0.9f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw delicate node dot
            drawCircle(
                color = lineBaseColor.copy(alpha = if (isConnected) 0.28f else 0.14f),
                radius = n1.radius,
                center = p1
            )
        }
    }
}
