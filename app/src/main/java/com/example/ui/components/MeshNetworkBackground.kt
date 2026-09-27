package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.cos
import kotlin.math.sin

/**
 * Geometric constellation wireframe mesh directly replicating the background structures
 * from vellorshoping.com (as seen in Screenshot_20260927_212746 and the video).
 *
 * Distinct charcoal/black nodes (vertices) connected by delicate crisp intersecting lines,
 * forming 3D polyhedral clusters across the viewport behind the content.
 */
@Composable
fun MeshNetworkBackground(
    modifier: Modifier = Modifier,
    isConnected: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_rotation")
    val angleRad by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831855f,
        animationSpec = infiniteRepeatable(
            animation = tween( durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle_rotation"
    )

    // Pre-calculated geometric polyhedral vertex clusters (matching the screenshots)
    // Cluster 1: Upper-center cluster (prominently seen behind hero and "Complete your order")
    val cluster1Base = remember {
        listOf(
            Offset(0.55f, 0.08f),
            Offset(0.72f, 0.05f),
            Offset(0.85f, 0.12f),
            Offset(0.68f, 0.15f),
            Offset(0.52f, 0.19f),
            Offset(0.62f, 0.24f),
            Offset(0.78f, 0.22f),
            Offset(0.92f, 0.18f),
            Offset(0.82f, 0.30f),
            Offset(0.65f, 0.33f),
            Offset(0.50f, 0.28f),
            Offset(0.40f, 0.18f),
            Offset(0.46f, 0.11f),
            Offset(0.74f, 0.36f),
            Offset(0.88f, 0.28f),
            Offset(0.96f, 0.35f),
            Offset(0.60f, 0.42f),
            Offset(0.75f, 0.46f)
        )
    }

    // Edges connecting vertices for Cluster 1 to form the intricate 3D faceted polyhedron
    val cluster1Edges = remember {
        listOf(
            0 to 1, 1 to 2, 0 to 3, 1 to 3, 2 to 3, 2 to 7, 3 to 6, 6 to 7,
            0 to 4, 3 to 4, 4 to 5, 3 to 5, 5 to 6, 6 to 8, 7 to 8, 8 to 14,
            7 to 14, 5 to 9, 8 to 9, 9 to 10, 4 to 10, 4 to 11, 0 to 12, 11 to 12,
            10 to 11, 8 to 13, 9 to 13, 13 to 14, 14 to 15, 8 to 15, 9 to 16,
            13 to 16, 13 to 17, 16 to 17, 1 to 7, 3 to 8, 5 to 10, 6 to 14
        )
    }

    // Cluster 2: Middle-left cluster (behind category cards / selected nodes)
    val cluster2Base = remember {
        listOf(
            Offset(0.08f, 0.45f),
            Offset(0.22f, 0.42f),
            Offset(0.35f, 0.47f),
            Offset(0.18f, 0.52f),
            Offset(0.05f, 0.56f),
            Offset(0.14f, 0.62f),
            Offset(0.28f, 0.58f),
            Offset(0.38f, 0.64f),
            Offset(0.25f, 0.70f),
            Offset(0.10f, 0.73f),
            Offset(0.02f, 0.65f),
            Offset(0.20f, 0.78f),
            Offset(0.34f, 0.75f)
        )
    }

    val cluster2Edges = remember {
        listOf(
            0 to 1, 1 to 2, 0 to 3, 1 to 3, 2 to 3, 2 to 6, 3 to 6,
            0 to 4, 3 to 4, 4 to 5, 3 to 5, 5 to 6, 6 to 7, 2 to 7,
            6 to 8, 7 to 8, 5 to 8, 5 to 9, 8 to 9, 4 to 10, 9 to 10,
            9 to 11, 8 to 11, 8 to 12, 7 to 12, 11 to 12
        )
    }

    // Cluster 3: Lower-right cluster (spans down the feed)
    val cluster3Base = remember {
        listOf(
            Offset(0.65f, 0.68f),
            Offset(0.80f, 0.65f),
            Offset(0.92f, 0.70f),
            Offset(0.75f, 0.75f),
            Offset(0.60f, 0.78f),
            Offset(0.70f, 0.84f),
            Offset(0.85f, 0.80f),
            Offset(0.95f, 0.86f),
            Offset(0.82f, 0.91f),
            Offset(0.68f, 0.94f)
        )
    }

    val cluster3Edges = remember {
        listOf(
            0 to 1, 1 to 2, 0 to 3, 1 to 3, 2 to 3, 2 to 6, 3 to 6,
            0 to 4, 3 to 4, 4 to 5, 3 to 5, 5 to 6, 6 to 7, 6 to 8,
            7 to 8, 5 to 8, 5 to 9, 8 to 9
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Clean white background
        drawRect(color = Color(0xFFFFFFFF))

        // Dynamic subtle drift offset
        val driftX = cos(angleRad) * 4f
        val driftY = sin(angleRad) * 4f

        fun drawCluster(
            vertices: List<Offset>,
            edges: List<Pair<Int, Int>>,
            alphaMultiplier: Float
        ) {
            val projected = vertices.map { pt ->
                Offset(pt.x * w + driftX, pt.y * h + driftY)
            }

            // Draw connecting lines with visible, sharp high-fashion opacity
            val baseLineColor = Color(0xFF18181B)
            edges.forEachIndexed { index, (fromIdx, toIdx) ->
                if (fromIdx < projected.size && toIdx < projected.size) {
                    val p1 = projected[fromIdx]
                    val p2 = projected[toIdx]
                    // Alternate line strength (0.16f to 0.38f) to create 3D faceted depth
                    val edgeAlpha = when (index % 4) {
                        0 -> 0.32f
                        1 -> 0.22f
                        2 -> 0.16f
                        else -> 0.26f
                    } * alphaMultiplier

                    drawLine(
                        color = baseLineColor.copy(alpha = edgeAlpha),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.2f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw distinct node vertices (dots at intersections as seen in Screenshot_20260927_212746)
            projected.forEachIndexed { i, pt ->
                val nodeAlpha = if (i % 3 == 0) 0.55f else 0.35f
                val radius = if (i % 4 == 0) 3.2f else 2.2f
                drawCircle(
                    color = Color(0xFF09090B).copy(alpha = nodeAlpha * alphaMultiplier),
                    radius = radius,
                    center = pt
                )
            }
        }

        // Draw the 3 geometric structure clusters clearly visible across the page
        drawCluster(cluster1Base, cluster1Edges, alphaMultiplier = 1.0f)
        drawCluster(cluster2Base, cluster2Edges, alphaMultiplier = 0.9f)
        drawCluster(cluster3Base, cluster3Edges, alphaMultiplier = 0.85f)
    }
}
