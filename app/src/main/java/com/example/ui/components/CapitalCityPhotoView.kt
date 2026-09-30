package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerLocation

@Composable
fun CapitalCityPhotoView(
    server: ServerLocation,
    modifier: Modifier = Modifier,
    darkenFactor: Float = 0.35f,
    showCoordinates: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF141416))
    ) {
        // Architectural geometric skyline backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Abstract silhouette towers
            val skylineBars = listOf(
                Triple(0.05f, 0.28f, 0.45f),
                Triple(0.18f, 0.42f, 0.35f),
                Triple(0.32f, 0.65f, 0.25f),
                Triple(0.48f, 0.52f, 0.30f),
                Triple(0.62f, 0.78f, 0.20f),
                Triple(0.75f, 0.45f, 0.32f),
                Triple(0.88f, 0.35f, 0.40f)
            )

            for (bar in skylineBars) {
                val bx = bar.first * w
                val bw = bar.third * w * 0.4f
                val bh = bar.second * h
                drawRect(
                    color = Color.White.copy(alpha = 0.04f),
                    topLeft = Offset(bx, h - bh),
                    size = androidx.compose.ui.geometry.Size(bw, bh)
                )
            }

            // Diagonal light ray
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = 2f
            )
        }

        // Vignette gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // Large stylized city code watermark
        Text(
            text = server.cityCode,
            color = Color.White.copy(alpha = 0.08f),
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 20.dp)
        )
    }
}
