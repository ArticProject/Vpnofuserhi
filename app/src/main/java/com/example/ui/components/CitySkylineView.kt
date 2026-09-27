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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerLocation

/**
 * High-Contrast Architectural Cityscape View (Monochrome Editorial Photography Style)
 * Renders recognizable capital city architectural landmarks with stark, editorial contrast.
 */
@Composable
fun CitySkylineView(
    server: ServerLocation,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F0F12))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Dramatic Sky Gradient (Monochrome stark lighting)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF27272A),
                        Color(0xFF141416),
                        Color(0xFF09090B)
                    )
                )
            )

            // 2. City Specific Landmark Architecture
            when (server.cityCode) {
                "ZRH" -> drawZurichLandmarks(w, h)
                "RKV" -> drawReykjavikLandmarks(w, h)
                "TYO" -> drawTokyoLandmarks(w, h)
                "AMS" -> drawAmsterdamLandmarks(w, h)
                "LON" -> drawLondonLandmarks(w, h)
                "FRA" -> drawFrankfurtLandmarks(w, h)
                "STO" -> drawStockholmLandmarks(w, h)
                "NYC" -> drawNewYorkLandmarks(w, h)
                "SIN" -> drawSingaporeLandmarks(w, h)
                "TOR" -> drawTorontoLandmarks(w, h)
                "SYD" -> drawSydneyLandmarks(w, h)
                else -> drawGenericMonoliths(w, h)
            }

            // 3. Ground / Waterfront Reflection Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF09090B).copy(alpha = 0.85f),
                        Color(0xFF09090B)
                    ),
                    startY = h * 0.72f,
                    endY = h
                )
            )
        }

        // Coordinates stamp in bottom right (editorial look)
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
            else -> "52.00° N · 0.00° E"
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = coords,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Zurich: Grossmünster Twin Towers + Limmat river bridge
private fun DrawScope.drawZurichLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFF4F4F6)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF3F3F46)

    // Distant Alpine Peaks
    val peakPath = Path().apply {
        moveTo(0f, h * 0.45f)
        lineTo(w * 0.25f, h * 0.32f)
        lineTo(w * 0.45f, h * 0.40f)
        lineTo(w * 0.70f, h * 0.28f)
        lineTo(w, h * 0.42f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(peakPath, color = darkTone.copy(alpha = 0.4f))

    // Grossmünster Twin Towers
    val towerWidth = w * 0.11f
    val towerHeight = h * 0.52f
    val t1X = w * 0.38f
    val t2X = w * 0.54f
    val baseY = h * 0.78f

    // Tower 1
    drawRect(color = midTone, topLeft = Offset(t1X, baseY - towerHeight), size = Size(towerWidth, towerHeight))
    val spire1 = Path().apply {
        moveTo(t1X, baseY - towerHeight)
        lineTo(t1X + towerWidth / 2f, baseY - towerHeight - h * 0.16f)
        lineTo(t1X + towerWidth, baseY - towerHeight)
        close()
    }
    drawPath(spire1, color = bldgColor)

    // Tower 2
    drawRect(color = midTone, topLeft = Offset(t2X, baseY - towerHeight), size = Size(towerWidth, towerHeight))
    val spire2 = Path().apply {
        moveTo(t2X, baseY - towerHeight)
        lineTo(t2X + towerWidth / 2f, baseY - towerHeight - h * 0.16f)
        lineTo(t2X + towerWidth, baseY - towerHeight)
        close()
    }
    drawPath(spire2, color = bldgColor)

    // Bridge / Quai across river
    drawRect(color = bldgColor.copy(alpha = 0.7f), topLeft = Offset(0f, baseY), size = Size(w, 4f))
}

// Reykjavik: Modernist Hallgrímskirkja stepped facade
private fun DrawScope.drawReykjavikLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFD4D4D8)
    val shadowTone = Color(0xFF52525B)

    val centerX = w * 0.5f
    val baseY = h * 0.82f

    // Stepped columns of Hallgrímskirkja
    val stepCount = 7
    val stepWidth = w * 0.045f
    val maxHeight = h * 0.65f

    for (i in 0 until stepCount) {
        val fraction = i / (stepCount - 1f)
        val colHeight = maxHeight * (0.35f + 0.65f * fraction)
        val offset = (stepCount - 1 - i) * stepWidth

        // Left column
        drawRect(
            color = if (i % 2 == 0) midTone else shadowTone,
            topLeft = Offset(centerX - offset - stepWidth, baseY - colHeight),
            size = Size(stepWidth - 2f, colHeight)
        )
        // Right column
        drawRect(
            color = if (i % 2 == 0) midTone else shadowTone,
            topLeft = Offset(centerX + offset, baseY - colHeight),
            size = Size(stepWidth - 2f, colHeight)
        )
    }

    // Central Tower Spire
    val spireWidth = w * 0.08f
    drawRect(
        color = bldgColor,
        topLeft = Offset(centerX - spireWidth / 2f, baseY - maxHeight),
        size = Size(spireWidth, maxHeight)
    )
    val spireTop = Path().apply {
        moveTo(centerX - spireWidth / 2f, baseY - maxHeight)
        lineTo(centerX, baseY - maxHeight - h * 0.14f)
        lineTo(centerX + spireWidth / 2f, baseY - maxHeight)
        close()
    }
    drawPath(spireTop, color = bldgColor)
}

// Tokyo: Tokyo Tower + High-rise skyline
private fun DrawScope.drawTokyoLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val accentRed = Color(0xFFF43F5E)
    val darkTone = Color(0xFF3F3F46)

    val baseY = h * 0.82f

    // Skyscraper silhouettes in background
    drawRect(color = darkTone, topLeft = Offset(w * 0.08f, baseY - h * 0.48f), size = Size(w * 0.18f, h * 0.48f))
    drawRect(color = darkTone.copy(alpha = 0.8f), topLeft = Offset(w * 0.68f, baseY - h * 0.55f), size = Size(w * 0.22f, h * 0.55f))

    // Tokyo Tower
    val towerBaseX = w * 0.46f
    val towerBaseW = w * 0.26f
    val towerHeight = h * 0.72f

    val towerPath = Path().apply {
        moveTo(towerBaseX - towerBaseW / 2f, baseY)
        lineTo(towerBaseX - 3f, baseY - towerHeight)
        lineTo(towerBaseX + 3f, baseY - towerHeight)
        lineTo(towerBaseX + towerBaseW / 2f, baseY)
        close()
    }
    drawPath(towerPath, color = bldgColor)

    // Red observation decks
    drawRect(color = accentRed, topLeft = Offset(towerBaseX - w * 0.08f, baseY - towerHeight * 0.45f), size = Size(w * 0.16f, 6f))
    drawRect(color = accentRed, topLeft = Offset(towerBaseX - w * 0.05f, baseY - towerHeight * 0.75f), size = Size(w * 0.10f, 5f))

    // Spire needle
    drawLine(
        color = Color.White,
        start = Offset(towerBaseX, baseY - towerHeight),
        end = Offset(towerBaseX, baseY - towerHeight - h * 0.12f),
        strokeWidth = 2.5f
    )
}

// Amsterdam: Dutch canal gabled houses
private fun DrawScope.drawAmsterdamLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFE4E4E7)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF52525B)

    val baseY = h * 0.75f
    val houseW = w * 0.18f

    // 5 Canal houses side by side
    val houseHeights = listOf(h * 0.48f, h * 0.56f, h * 0.52f, h * 0.58f, h * 0.46f)
    for (i in 0 until 5) {
        val x = w * 0.05f + i * houseW
        val hh = houseHeights[i]

        // Base house
        drawRect(
            color = if (i % 2 == 0) bldgColor else midTone,
            topLeft = Offset(x, baseY - hh),
            size = Size(houseW - 4f, hh)
        )

        // Stepped / Bell Gable roof
        val gable = Path().apply {
            moveTo(x, baseY - hh)
            lineTo(x + (houseW - 4f) / 2f, baseY - hh - h * 0.10f)
            lineTo(x + houseW - 4f, baseY - hh)
            close()
        }
        drawPath(gable, color = if (i % 2 == 0) midTone else darkTone)

        // Window grid
        for (row in 1..4) {
            val wy = baseY - hh + row * (hh / 5f)
            drawRect(color = Color(0xFF18181B), topLeft = Offset(x + 4f, wy), size = Size(houseW * 0.35f, 5f))
            drawRect(color = Color(0xFF18181B), topLeft = Offset(x + houseW * 0.52f, wy), size = Size(houseW * 0.35f, 5f))
        }
    }

    // Canal water reflection line
    drawRect(color = Color.White.copy(alpha = 0.25f), topLeft = Offset(0f, baseY + 4f), size = Size(w, 2f))
}

// London: Big Ben / Elizabeth Tower + Westminster
private fun DrawScope.drawLondonLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFF4F4F6)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF3F3F46)

    val baseY = h * 0.80f

    // Westminster Palace Hall
    drawRect(color = darkTone, topLeft = Offset(w * 0.05f, baseY - h * 0.26f), size = Size(w * 0.55f, h * 0.26f))

    // Big Ben Tower
    val towerX = w * 0.62f
    val towerW = w * 0.16f
    val towerH = h * 0.68f

    drawRect(color = midTone, topLeft = Offset(towerX, baseY - towerH), size = Size(towerW, towerH))

    // Clock Face circle
    drawCircle(
        color = bldgColor,
        radius = towerW * 0.35f,
        center = Offset(towerX + towerW / 2f, baseY - towerH + towerW * 0.6f)
    )

    // Gothic Spire
    val spire = Path().apply {
        moveTo(towerX, baseY - towerH)
        lineTo(towerX + towerW / 2f, baseY - towerH - h * 0.18f)
        lineTo(towerX + towerW, baseY - towerH)
        close()
    }
    drawPath(spire, color = bldgColor)
}

// Frankfurt: Financial high-rise monoliths
private fun DrawScope.drawFrankfurtLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF3F3F46)

    val baseY = h * 0.82f

    drawRect(color = darkTone, topLeft = Offset(w * 0.10f, baseY - h * 0.52f), size = Size(w * 0.18f, h * 0.52f))
    drawRect(color = midTone, topLeft = Offset(w * 0.32f, baseY - h * 0.70f), size = Size(w * 0.22f, h * 0.70f))
    drawRect(color = bldgColor, topLeft = Offset(w * 0.58f, baseY - h * 0.62f), size = Size(w * 0.20f, h * 0.62f))
    drawRect(color = darkTone, topLeft = Offset(w * 0.80f, baseY - h * 0.44f), size = Size(w * 0.15f, h * 0.44f))

    // Commerzbank Tower Spire
    drawLine(
        color = Color.White,
        start = Offset(w * 0.43f, baseY - h * 0.70f),
        end = Offset(w * 0.43f, baseY - h * 0.84f),
        strokeWidth = 2.5f
    )
}

// Stockholm: Spired waterfront
private fun DrawScope.drawStockholmLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFE4E4E7)
    val midTone = Color(0xFFA1A1AA)
    val baseY = h * 0.78f

    drawRect(color = midTone, topLeft = Offset(w * 0.12f, baseY - h * 0.35f), size = Size(w * 0.75f, h * 0.35f))
    // Riddarholmen Church Spire
    val spire = Path().apply {
        moveTo(w * 0.44f, baseY - h * 0.35f)
        lineTo(w * 0.50f, baseY - h * 0.75f)
        lineTo(w * 0.56f, baseY - h * 0.35f)
        close()
    }
    drawPath(spire, color = bldgColor)
}

// New York: Empire State + canyons
private fun DrawScope.drawNewYorkLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF52525B)
    val baseY = h * 0.82f

    // Left skyscrapers
    drawRect(color = darkTone, topLeft = Offset(w * 0.08f, baseY - h * 0.55f), size = Size(w * 0.20f, h * 0.55f))
    // Empire State Building
    val esbX = w * 0.38f
    val esbW = w * 0.24f
    drawRect(color = midTone, topLeft = Offset(esbX, baseY - h * 0.50f), size = Size(esbW, h * 0.50f))
    drawRect(color = bldgColor, topLeft = Offset(esbX + esbW * 0.18f, baseY - h * 0.68f), size = Size(esbW * 0.64f, h * 0.18f))
    // Mast
    drawLine(
        color = Color.White,
        start = Offset(esbX + esbW / 2f, baseY - h * 0.68f),
        end = Offset(esbX + esbW / 2f, baseY - h * 0.85f),
        strokeWidth = 3f
    )
    // Right skyscrapers
    drawRect(color = darkTone, topLeft = Offset(w * 0.68f, baseY - h * 0.58f), size = Size(w * 0.22f, h * 0.58f))
}

// Singapore: Marina Bay Sands silhouette
private fun DrawScope.drawSingaporeLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFA1A1AA)
    val baseY = h * 0.78f

    // 3 Towers
    val tW = w * 0.12f
    val tH = h * 0.48f
    drawRect(color = midTone, topLeft = Offset(w * 0.22f, baseY - tH), size = Size(tW, tH))
    drawRect(color = midTone, topLeft = Offset(w * 0.44f, baseY - tH), size = Size(tW, tH))
    drawRect(color = midTone, topLeft = Offset(w * 0.66f, baseY - tH), size = Size(tW, tH))

    // SkyPark Boat on top
    val boat = Path().apply {
        moveTo(w * 0.15f, baseY - tH)
        lineTo(w * 0.85f, baseY - tH - 6f)
        lineTo(w * 0.82f, baseY - tH - 12f)
        lineTo(w * 0.18f, baseY - tH - 8f)
        close()
    }
    drawPath(boat, color = bldgColor)
}

// Toronto: CN Tower
private fun DrawScope.drawTorontoLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val darkTone = Color(0xFF3F3F46)
    val baseY = h * 0.82f

    drawRect(color = darkTone, topLeft = Offset(w * 0.12f, baseY - h * 0.45f), size = Size(w * 0.22f, h * 0.45f))
    drawRect(color = darkTone, topLeft = Offset(w * 0.65f, baseY - h * 0.48f), size = Size(w * 0.22f, h * 0.48f))

    // CN Tower Shaft
    val towerX = w * 0.48f
    drawLine(color = bldgColor, start = Offset(towerX, baseY), end = Offset(towerX, baseY - h * 0.80f), strokeWidth = 5f)
    // Pod
    drawOval(color = bldgColor, topLeft = Offset(towerX - 12f, baseY - h * 0.62f), size = Size(24f, 10f))
}

// Sydney: Opera House shells
private fun DrawScope.drawSydneyLandmarks(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFA1A1AA)
    val baseY = h * 0.78f

    // 3 interlocking shells
    val shell1 = Path().apply {
        moveTo(w * 0.15f, baseY)
        cubicTo(w * 0.25f, baseY - h * 0.35f, w * 0.42f, baseY - h * 0.42f, w * 0.45f, baseY)
        close()
    }
    drawPath(shell1, color = midTone)

    val shell2 = Path().apply {
        moveTo(w * 0.32f, baseY)
        cubicTo(w * 0.42f, baseY - h * 0.48f, w * 0.62f, baseY - h * 0.52f, w * 0.65f, baseY)
        close()
    }
    drawPath(shell2, color = bldgColor)

    val shell3 = Path().apply {
        moveTo(w * 0.52f, baseY)
        cubicTo(w * 0.62f, baseY - h * 0.38f, w * 0.78f, baseY - h * 0.42f, w * 0.82f, baseY)
        close()
    }
    drawPath(shell3, color = midTone)
}

// Fallback generic modernist monoliths
private fun DrawScope.drawGenericMonoliths(w: Float, h: Float) {
    val bldgColor = Color(0xFFFFFFFF)
    val midTone = Color(0xFFA1A1AA)
    val darkTone = Color(0xFF3F3F46)
    val baseY = h * 0.80f

    drawRect(color = darkTone, topLeft = Offset(w * 0.15f, baseY - h * 0.40f), size = Size(w * 0.20f, h * 0.40f))
    drawRect(color = bldgColor, topLeft = Offset(w * 0.40f, baseY - h * 0.62f), size = Size(w * 0.22f, h * 0.62f))
    drawRect(color = midTone, topLeft = Offset(w * 0.66f, baseY - h * 0.48f), size = Size(w * 0.20f, h * 0.48f))
}
