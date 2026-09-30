package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.MeshNetworkBackground
import com.example.ui.components.OpenInfinityLogo
import com.example.ui.components.bounceClick
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceInner
import com.example.ui.theme.VellorEmerald
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF09090B) else Color.White)
    ) {
        // Living animated astral mesh network background
        MeshNetworkBackground(
            modifier = Modifier.fillMaxSize(),
            isDarkTheme = isDarkTheme,
            isConnected = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar: Brand, Step Counter, and Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OpenInfinityLogo(
                        size = 30.dp,
                        color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                        strokeWidth = 3.2f
                    )
                    Text(
                        text = "Vellor",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Luxury Step Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                            .border(
                                1.dp,
                                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0${pagerState.currentPage + 1} / 04",
                            color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Skip button with tactile bounce
                    if (pagerState.currentPage < 3) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .bounceClick(scaleDown = 0.94f) { onFinishOnboarding() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("onboarding_skip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRu) "Пропустить" else "Skip",
                                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 2. Main HorizontalPager with Luxury Cards
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 16.dp
            ) { page ->
                when (page) {
                    0 -> LuxuryWelcomeCard(
                        isRu = isRu,
                        isDarkTheme = isDarkTheme
                    )
                    1 -> LuxuryLanguageSelectionCard(
                        currentLanguage = currentLanguage,
                        onSelectLanguage = onSelectLanguage,
                        isRu = isRu,
                        isDarkTheme = isDarkTheme
                    )
                    2 -> LuxuryThemeCustomizationCard(
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                        isRu = isRu
                    )
                    3 -> LuxurySecurityActivationCard(
                        currentLanguage = currentLanguage,
                        isDarkTheme = isDarkTheme,
                        isRu = isRu
                    )
                }
            }

            // 3. Bottom Controls: Animated Indicator Dots & Directional Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Interactive Luxury Page Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isSelected = index == pagerState.currentPage
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 7.dp,
                            animationSpec = spring(dampingRatio = 0.72f, stiffness = 400f),
                            label = "onboard_dot_w"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (isDarkTheme) Color.White else Color(0xFF09090B)
                            } else {
                                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA)
                            },
                            animationSpec = spring(stiffness = 400f),
                            label = "onboard_dot_c"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                                .bounceClick(scaleDown = 0.90f) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                // Dual Action Buttons: Back (if > 0) + Primary Continue / Finish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(27.dp))
                                .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
                                .border(
                                    1.dp,
                                    if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                                    RoundedCornerShape(27.dp)
                                )
                                .bounceClick(scaleDown = 0.94f) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                                .testTag("onboarding_back"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isRu) "Назад" else "Back",
                                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Primary Action Button (Continue / Finish)
                    Box(
                        modifier = Modifier
                            .weight(if (pagerState.currentPage > 0) 0.65f else 1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(if (isDarkTheme) Color.White else Color(0xFF09090B))
                            .bounceClick(scaleDown = 0.95f) {
                                if (pagerState.currentPage < 3) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    onFinishOnboarding()
                                }
                            }
                            .testTag("onboarding_next"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (pagerState.currentPage < 3) {
                                    if (isRu) "Продолжить" else "Continue"
                                } else {
                                    if (isRu) "Войти в туннель" else "Enter Sovereign Tunnel"
                                },
                                color = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Page 0: Luxury Manifesto & Welcome Card
 */
@Composable
private fun LuxuryWelcomeCard(
    isRu: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
            .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
            .border(
                1.dp,
                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Architectural Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                        .border(0.8.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isRu) "СУВЕРЕННЫЙ ТУННЕЛЬ · ШЛЮЗ 10G" else "HAUTE PRIVACY · SOVEREIGN GATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                        letterSpacing = 1.3.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isRu) "Приватность с\nчетким\nсилуэтом."
                           else "Everyday privacy with\na\nsharp silhouette.",
                    style = MaterialTheme.typography.displayMedium,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    letterSpacing = (-1.2).sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isRu)
                        "Бескомпромиссный, суверенный криптографический туннель. Прямая маршрутизация трафика вашего устройства через мировые столицы."
                    else
                        "A bespoke sovereign tunnel engineered with zero compromise. Handcrafted encryption silhouettes for high-discretion personal routing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Luxury Feature Highlights
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LuxuryFeatureRow(
                    icon = Icons.Filled.Speed,
                    title = if (isRu) "WireGuard 10 Gbps" else "WireGuard 10 Gbps Peering",
                    subtitle = if (isRu) "Мгновенное рукопожатие без задержек" else "Sub-millisecond cryptographic handshake",
                    isDarkTheme = isDarkTheme
                )
                LuxuryFeatureRow(
                    icon = Icons.Filled.VpnLock,
                    title = if (isRu) "Zero-Knowledge Logging" else "Zero-Knowledge Architecture",
                    subtitle = if (isRu) "RAM-only серверы без записи активности" else "RAM-disk ephemeral nodes with zero trace",
                    isDarkTheme = isDarkTheme
                )
                LuxuryFeatureRow(
                    icon = Icons.Filled.Shield,
                    title = if (isRu) "Аппаратный Kill Switch" else "Hardware-Grade Kill Switch",
                    subtitle = if (isRu) "Защита от утечек DNS и WebRTC" else "Instant telemetry intercept on link drop",
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

/**
 * Page 1: Luxury Language Selection Cards
 */
@Composable
private fun LuxuryLanguageSelectionCard(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isRu: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
            .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
            .border(
                1.dp,
                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                    .border(0.8.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isRu) "ЛОКАЛИЗАЦИЯ ИНТЕРФЕЙСА · ШАГ 02" else "BESPOKE LOCALIZATION · STEP 02",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    letterSpacing = 1.3.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isRu) "Выберите язык" else "Select Language",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = if (isRu) "Адаптируйте терминологию под вашу рабочую среду."
                       else "Tailor interface terminology to your preferred sovereign workspace.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Luxury Language Card List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == currentLanguage

                    val cardBorderCol by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (isDarkTheme) Color.White else Color(0xFF09090B)
                        } else {
                            if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA)
                        },
                        animationSpec = spring(stiffness = 400f),
                        label = "lang_card_border"
                    )

                    val cardBorderW by animateDpAsState(
                        targetValue = if (isSelected) 1.8.dp else 1.dp,
                        animationSpec = spring(stiffness = 400f),
                        label = "lang_card_w"
                    )

                    val cardBg by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (isDarkTheme) DarkSurfaceInner else Color(0xFFF9F9FB)
                        } else {
                            if (isDarkTheme) DarkSurfaceInner.copy(alpha = 0.5f) else Color.White
                        },
                        animationSpec = spring(stiffness = 400f),
                        label = "lang_card_bg"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(cardBg)
                            .border(cardBorderW, cardBorderCol, RoundedCornerShape(18.dp))
                            .bounceClick(scaleDown = 0.96f) { onSelectLanguage(lang) }
                            .padding(horizontal = 16.dp, vertical = 13.dp)
                            .testTag("lang_option_${lang.code}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Luxury Flag Emblem
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE5E5EA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang.flag,
                                        fontSize = 19.sp
                                    )
                                }

                                Column {
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (lang.subtitle.isNotBlank()) lang.subtitle else "${lang.code.uppercase()} · SOVEREIGN DIALECT",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Selection Jewel
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            if (isDarkTheme) Color.White else Color(0xFF09090B)
                                        } else Color.Transparent
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) (if (isDarkTheme) Color.White else Color(0xFF09090B))
                                        else (if (isDarkTheme) DarkSurfaceBorder else Color(0xFFD4D4D8)),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Page 2: Luxury Theme Customization Cards
 */
@Composable
private fun LuxuryThemeCustomizationCard(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    isRu: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
            .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
            .border(
                1.dp,
                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                    .border(0.8.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isRu) "ВИЗУАЛЬНЫЙ СИЛУЭТ · ШАГ 03" else "VISUAL SILHOUETTE · STEP 03",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    letterSpacing = 1.3.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isRu) "Тема оформления" else "Curate Appearance",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = if (isRu) "Два контрастных профиля, созданных для максимальной четкости и дискретности."
                       else "Two contrasting architectural palettes crafted for daytime focus and nocturnal discretion.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Two Luxury Theme Cards
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // 1. Obsidian Noir (Dark Luxury Card)
                LuxuryThemeCardItem(
                    title = if (isRu) "Obsidian Noir (Тёмная)" else "Obsidian Noir",
                    tagline = if (isRu) "Глубокий OLED черный · Максимальная скрытность" else "OLED Pure Black · Maximum Discretion",
                    description = if (isRu) "Абсолютно черный фон без излучения, титановые контуры и платиновые акценты."
                                  else "True zero-emission OLED blacks with titanium contours and platinum typography.",
                    isSelected = isDarkTheme,
                    cardThemeDark = true,
                    onClick = { onToggleDarkTheme(true) }
                )

                // 2. Alabaster Blanc (Light Luxury Card)
                LuxuryThemeCardItem(
                    title = if (isRu) "Alabaster Blanc (Светлая)" else "Alabaster Blanc",
                    tagline = if (isRu) "Скандинавский фарфор · Галерейная четкость" else "Scandinavian Gallery Light · High Contrast",
                    description = if (isRu) "Чистый белый силуэт с глубоким ониксовым контрастом и бескомпромиссной четкостью."
                                  else "Crisp architectural white palette with jet onyx contrast and gallery-level precision.",
                    isSelected = !isDarkTheme,
                    cardThemeDark = false,
                    onClick = { onToggleDarkTheme(false) }
                )
            }
        }
    }
}

/**
 * Individual Luxury Theme Card with miniature dial preview
 */
@Composable
private fun LuxuryThemeCardItem(
    title: String,
    tagline: String,
    description: String,
    isSelected: Boolean,
    cardThemeDark: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (cardThemeDark) Color.White else Color(0xFF09090B)
        } else {
            if (cardThemeDark) Color(0xFF27272A) else Color(0xFFE5E5EA)
        },
        animationSpec = spring(stiffness = 400f),
        label = "theme_card_border"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = spring(stiffness = 400f),
        label = "theme_card_border_w"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (cardThemeDark) Color(0xFF0D0D11) else Color(0xFFFAFAFC))
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .bounceClick(scaleDown = 0.96f, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (cardThemeDark) Color(0xFF18181B) else Color.White)
                            .border(1.dp, if (cardThemeDark) Color(0xFF27272A) else Color(0xFFE5E5EA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (cardThemeDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = if (cardThemeDark) Color.White else Color(0xFF09090B),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (cardThemeDark) Color.White else Color(0xFF09090B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (cardThemeDark) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (cardThemeDark) Color(0xFF71717A) else Color(0xFFA1A1AA),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Jewel Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            if (cardThemeDark) Color.White else Color(0xFF09090B)
                        } else Color.Transparent
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) (if (cardThemeDark) Color.White else Color(0xFF09090B))
                        else (if (cardThemeDark) Color(0xFF3F3F46) else Color(0xFFD4D4D8)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = if (cardThemeDark) Color(0xFF09090B) else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Page 3: Luxury Security Verification & Handshake Card
 */
@Composable
private fun LuxurySecurityActivationCard(
    currentLanguage: AppLanguage,
    isDarkTheme: Boolean,
    isRu: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
            .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
            .border(
                1.dp,
                if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                        .border(0.8.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isRu) "ФИНАЛЬНАЯ ГОТОВНОСТЬ · ШАГ 04" else "FINAL ARMED STATE · STEP 04",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                        letterSpacing = 1.3.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isRu) "Туннель готов к запуску" else "Sovereign Tunnel Armed",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = if (isRu)
                        "Ключи шифрования сгенерированы локально в защищенном анклаве устройства."
                    else
                        "Ephemeral cryptographic keys generated in device secure enclave. Zero leakage verified.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Summary verification plate
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecurityVerificationRow(
                        label = if (isRu) "Язык интерфейса" else "Dialect",
                        value = "${currentLanguage.flag} ${langTitle(currentLanguage)}",
                        isDarkTheme = isDarkTheme
                    )
                    SecurityVerificationRow(
                        label = if (isRu) "Тема оформления" else "Appearance Profile",
                        value = if (isDarkTheme) "Obsidian Noir (OLED)" else "Alabaster Blanc",
                        isDarkTheme = isDarkTheme
                    )
                    SecurityVerificationRow(
                        label = if (isRu) "Криптографический шифр" else "Tunnel Cipher",
                        value = "ChaCha20-Poly1305 (256-bit)",
                        isDarkTheme = isDarkTheme
                    )
                    SecurityVerificationRow(
                        label = if (isRu) "Аварийная защита" else "Hardware Kill Switch",
                        value = if (isRu) "Включена (Fail-Safe)" else "Armed (Fail-Safe)",
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            // Green status indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VellorEmerald)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRu) "ВСЕ УЗЛЫ ГОТОВЫ К ПОДКЛЮЧЕНИЮ" else "GLOBAL NETWORK ONLINE & READY",
                    color = VellorEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
            }
        }
    }
}

private fun langTitle(lang: AppLanguage): String = when (lang) {
    AppLanguage.SYSTEM -> "System Auto"
    AppLanguage.ENGLISH -> "English"
    AppLanguage.RUSSIAN -> "Русский"
    AppLanguage.JAPANESE -> "日本語"
    AppLanguage.GERMAN -> "Deutsch"
}

@Composable
private fun SecurityVerificationRow(
    label: String,
    value: String,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF09090B)
        )
    }
}

@Composable
private fun LuxuryFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDarkTheme) Color(0xFF18181B) else Color.White)
                .border(1.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkTheme) Color.White else Color(0xFF09090B),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF09090B)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
            )
        }
    }
}
