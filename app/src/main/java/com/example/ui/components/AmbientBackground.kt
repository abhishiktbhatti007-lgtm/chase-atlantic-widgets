package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.AmbientMode
import com.example.data.model.ThemeAccent
import com.example.ui.theme.PitchBlack

@Composable
fun AmbientBackground(
    ambientMode: AmbientMode,
    themeAccent: ThemeAccent,
    isNightModeOnly: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    if (isNightModeOnly || ambientMode == AmbientMode.PURE_AMOLED) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(PitchBlack),
            content = content
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
    ) {
        if (ambientMode == AmbientMode.SMOKE_GLASS) {
            // Ambient Generated Wallpaper
            Image(
                painter = painterResource(id = R.drawable.img_ambient_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.38f),
                contentScale = ContentScale.Crop
            )

            // Obsidian Glass Vignette Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xDD000000),
                                Color(0x99000000),
                                Color(0xFF040407)
                            )
                        )
                    )
            )
        } else {
            // Liquid Neon Pulse
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(themeAccent.glow.copy(alpha = pulseAlpha), Color.Transparent),
                                center = Offset(size.width * 0.2f, size.height * 0.2f),
                                radius = size.width * 0.9f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(themeAccent.secondary.copy(alpha = pulseAlpha * 0.6f), Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.7f),
                                radius = size.width * 0.8f
                            )
                        )
                    }
            )
        }

        // Overlay Content
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}
