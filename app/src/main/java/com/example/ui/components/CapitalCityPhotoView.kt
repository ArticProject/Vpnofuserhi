package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.ServerLocation

/**
 * Renders an authentic high-resolution architectural photo of the capital city.
 * Gracefully layers fallback art, monochrome dark gradient, and coordinates stamp.
 */
@Composable
fun CapitalCityPhotoView(
    server: ServerLocation,
    modifier: Modifier = Modifier,
    darkenFactor: Float = 0.35f,
    showCoordinates: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F0F12))
    ) {
        // Fallback Vector Silhouette / Skyline Art
        CitySkylineView(
            server = server,
            modifier = Modifier.fillMaxSize()
        )

        // Real High-Resolution Capital City Photo via Coil in pure Black & White
        if (server.photoUrl.isNotBlank()) {
            val monochromeMatrix = remember { ColorMatrix().apply { setToSaturation(0f) } }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(server.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${server.city} architectural photo",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(monochromeMatrix),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Editorial Lighting / Contrast Gradients (Luxury Lookbook Aesthetics)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = darkenFactor * 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = darkenFactor + 0.35f)
                        )
                    )
                )
        )

        // Coordinates stamp in bottom right
        if (showCoordinates) {
            val coords = when (server.cityCode) {
                "ZRH" -> "47.37° N · 8.54° E"
                "RKV" -> "64.14° N · 21.94° W"
                "TYO" -> "35.67° N · 139.65° E"
                "AMS" -> "52.36° N · 4.90° E"
                "LON" -> "51.50° N · 0.12° W"
                "FRA" -> "50.11° N · 8.68° E"
                "STO" -> "59.32° N · 18.06° E"
                "NYC" -> "40.71° N · 74.00° W"
                "SIN" -> "1.35° N · 103.81° E"
                "TOR" -> "43.65° N · 79.38° W"
                "SYD" -> "33.86° S · 151.20° E"
                else -> "CAPITAL VIEW"
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = coords,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
