package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Single-run physical Cosmos animation:
 * 8 dots launch up into an orbital ring, spin at peak, fall back to baseline,
 * bounce with squash-damping, and rest on the baseline without repeating.
 */
@Composable
fun CosmosOneShotDotsAnimation(
    playTrigger: Int,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    dotColor: Color = if (isDarkTheme) Color.White else Color(0xFF14171A)
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(playTrigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    val currentT = progress.value

    Canvas(modifier = modifier.size(width = 160.dp, height = 90.dp)) {
        val centerX = size.width / 2f
        val baselineY = size.height * 0.78f
        val dotRadius = 4.2.dp.toPx()
        val numDots = 8

        val liftY: Float
        val ringRadius: Float
        val rotationAngle: Float
        val horizontalSpread: Float

        when {
            currentT < 0.12f -> {
                // Initial resting state on baseline
                liftY = 0f
                ringRadius = 0f
                rotationAngle = 0f
                horizontalSpread = 1f
            }
            currentT < 0.45f -> {
                // Spring ascent into circular constellation
                val t = (currentT - 0.12f) / 0.33f
                val springT = sin(t * (PI.toFloat() / 2f))
                val overshoot = if (t > 0.8f) sin((t - 0.8f) / 0.2f * PI.toFloat()) * 4f else 0f
                liftY = (springT * 38.dp.toPx()) + overshoot
                ringRadius = springT * 22.dp.toPx()
                rotationAngle = t * 45f
                horizontalSpread = 1f - springT
            }
            currentT < 0.70f -> {
                // Orbital spin at apex
                val t = (currentT - 0.45f) / 0.25f
                liftY = 38.dp.toPx() + sin(t * PI.toFloat() * 2f) * 1.5f
                ringRadius = 22.dp.toPx()
                rotationAngle = 45f + (t * 180f)
                horizontalSpread = 0f
            }
            currentT < 0.88f -> {
                // Fall back down to baseline with gravity
                val t = (currentT - 0.70f) / 0.18f
                val fallT = t * t
                liftY = (1f - fallT) * 38.dp.toPx()
                ringRadius = (1f - fallT) * 22.dp.toPx()
                rotationAngle = 225f + (t * 45f)
                horizontalSpread = fallT
            }
            currentT < 1.0f -> {
                // Impact squash & settle on baseline
                val t = (currentT - 0.88f) / 0.12f
                val squash = sin(t * PI.toFloat()) * 5.dp.toPx()
                liftY = 0f
                ringRadius = 0f
                rotationAngle = 270f
                horizontalSpread = 1f + (squash / 10f)
            }
            else -> {
                // Completed: dots lie and rest calmly on baseline
                liftY = 0f
                ringRadius = 0f
                rotationAngle = 270f
                horizontalSpread = 1f
            }
        }

        // Draw 8 dots
        for (i in 0 until numDots) {
            val angleDeg = (i * (360f / numDots)) + rotationAngle
            val angleRad = angleDeg * (PI.toFloat() / 180f)

            val px: Float
            val py: Float

            if (ringRadius > 1f) {
                px = centerX + cos(angleRad) * ringRadius
                py = baselineY - liftY + sin(angleRad) * ringRadius
            } else {
                val offsetIndex = i - (numDots - 1) / 2f
                val spacing = 7.dp.toPx() * horizontalSpread
                px = centerX + (offsetIndex * spacing)
                val bounceJitter = if (currentT in 0.88f..0.98f) sin((i + currentT * 10f)) * 1.5f else 0f
                py = baselineY + bounceJitter
            }

            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(px, py)
            )
        }
    }
}

/**
 * Full-screen modal overlay with blurred backdrop.
 * Any tap on the blurred backdrop or outside dismisses the overlay.
 */
@Composable
fun CosmosErrorOverlay(
    visible: Boolean,
    title: String,
    subtitle: String,
    buttonText: String = "Попробовать снова",
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true
) {
    if (!visible) return

    var triggerCount by remember { mutableStateOf(0) }
    LaunchedEffect(visible) {
        if (visible) {
            triggerCount++
        }
    }

    val cardBg = if (isDarkTheme) Color(0xFF141210) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) Color(0xFF2C2723) else Color(0xFFE5E2DC)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF09090B)
    val textSecondary = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
    val btnBg = if (isDarkTheme) Color.White else Color(0xFF09090B)
    val btnText = if (isDarkTheme) Color(0xFF09090B) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cosmos_error_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Blurred backdrop layer - tapping anywhere dismisses the overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .blur(18.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )

        // Center card with spring animation
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(cardBg)
                .border(1.dp, cardBorder, RoundedCornerShape(26.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps on the card so it doesn't dismiss */ }
                .padding(24.dp)
        ) {
            // Dismiss icon in top right
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Single-run 60fps Cosmos dots
                CosmosOneShotDotsAnimation(
                    playTrigger = triggerCount,
                    isDarkTheme = isDarkTheme,
                    dotColor = textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = subtitle,
                    color = textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Action button (re-triggers animation and executes retry)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(btnBg)
                        .bounceClick(scaleDown = 0.93f) {
                            triggerCount++
                            onRetry()
                        }
                        .padding(horizontal = 30.dp, vertical = 13.dp)
                        .testTag("cosmos_overlay_retry_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        color = btnText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

/**
 * Cosmos App Launch Splash Screen:
 * Displayed for ~2.3 seconds on app launch with the bouncing physical 8 dots animation.
 */
@Composable
fun CosmosAppSplashScreen(
    visible: Boolean,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    onFinished: () -> Unit
) {
    if (!visible) return

    val isRu = currentLanguage == AppLanguage.RUSSIAN
    val bgColor = if (isDarkTheme) Color(0xFF09090B) else Color.White
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF09090B)
    val textSecondary = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2300)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cosmos physical 8-dot animation
            CosmosOneShotDotsAnimation(
                playTrigger = 1,
                isDarkTheme = isDarkTheme,
                dotColor = textPrimary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Name
            Text(
                text = "VELLOR",
                color = textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle status
            Text(
                text = if (isRu) "ИНИЦИАЛИЗАЦИЯ ШЛЮЗА..." else "INITIALIZING SECURE ENCLAVE...",
                color = textSecondary.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp
            )
        }
    }
}
