package com.example.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockStyle
import com.example.data.model.ThemeAccent
import com.example.ui.components.GlassCard
import com.example.ui.components.WidgetHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.TimeState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockWidget(
    timeState: TimeState,
    clockStyle: ClockStyle,
    themeAccent: ThemeAccent,
    onCycleStyle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "clock_pulse")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colon_blink"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clock_widget"),
        glowColor = themeAccent.glow,
        onClick = onCycleStyle
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WidgetHeader(
                tag = "01 // TIME",
                title = clockStyle.displayName,
                accentColor = themeAccent.primary,
                action = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "STYLE ⇄",
                            color = ChromeMid,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (clockStyle) {
                ClockStyle.OVERSIZED_DIGITAL -> {
                    OversizedDigitalClock(
                        timeState = timeState,
                        themeAccent = themeAccent,
                        colonAlpha = colonAlpha
                    )
                }
                ClockStyle.MINIMAL_ANALOG -> {
                    MinimalAnalogClock(
                        timeState = timeState,
                        themeAccent = themeAccent
                    )
                }
                ClockStyle.NEON_CYBER -> {
                    NeonCyberClock(
                        timeState = timeState,
                        themeAccent = themeAccent
                    )
                }
                ClockStyle.CHROME_STACK -> {
                    ChromeStackClock(
                        timeState = timeState,
                        themeAccent = themeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub info strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x330A0A10))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = timeState.dayOfWeek,
                        color = themeAccent.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(text = "•", color = ChromeDark, fontSize = 10.sp)
                    Text(
                        text = timeState.formattedDate,
                        color = ChromeBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (timeState.isLateNight) CrimsonNeon else AmberWarm)
                    )
                    Text(
                        text = if (timeState.isLateNight) "LATE NIGHT" else "DAYLIGHT",
                        color = ChromeMid,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OversizedDigitalClock(
    timeState: TimeState,
    themeAccent: ThemeAccent,
    colonAlpha: Float
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hours
        Text(
            text = timeState.hour12,
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = ChromeHighlight,
            letterSpacing = (-3).sp,
            lineHeight = 72.sp
        )

        // Pulsing Colon
        Text(
            text = ":",
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Monospace,
            color = themeAccent.primary.copy(alpha = colonAlpha),
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            lineHeight = 64.sp
        )

        // Minutes
        Text(
            text = timeState.minute,
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = ChromeHighlight,
            letterSpacing = (-3).sp,
            lineHeight = 72.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Seconds and AM/PM stack
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themeAccent.primary.copy(alpha = 0.2f))
                    .border(1.dp, themeAccent.primary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = timeState.amPm,
                    color = themeAccent.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${timeState.second}s",
                color = ChromeMid,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun MinimalAnalogClock(
    timeState: TimeState,
    themeAccent: ThemeAccent
) {
    val sec = timeState.second.toIntOrNull() ?: 0
    val min = timeState.minute.toIntOrNull() ?: 0
    val hour = timeState.hour12.toIntOrNull() ?: 0

    Box(
        modifier = Modifier
            .size(190.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Outer dial ring
            drawCircle(
                color = Color(0x33FFFFFF),
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner subtle gradient ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0x1AFFFFFF), Color(0x33000000)),
                    center = center,
                    radius = radius
                ),
                radius = radius - 4.dp.toPx()
            )

            // 12 hour ticks
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30 - 90).toDouble())
                val isMajor = i % 3 == 0
                val tickLength = if (isMajor) 14.dp.toPx() else 6.dp.toPx()
                val tickColor = if (isMajor) themeAccent.primary else Color(0x66FFFFFF)
                val tickWidth = if (isMajor) 2.5.dp.toPx() else 1.dp.toPx()

                val startX = center.x + (radius - tickLength - 4.dp.toPx()) * cos(angle).toFloat()
                val startY = center.y + (radius - tickLength - 4.dp.toPx()) * sin(angle).toFloat()
                val endX = center.x + (radius - 4.dp.toPx()) * cos(angle).toFloat()
                val endY = center.y + (radius - 4.dp.toPx()) * sin(angle).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }

            // Hour hand
            val hourAngle = Math.toRadians(((hour % 12 + min / 60f) * 30 - 90).toDouble())
            val hourLength = radius * 0.5f
            drawLine(
                color = ChromeHighlight,
                start = center,
                end = Offset(
                    center.x + hourLength * cos(hourAngle).toFloat(),
                    center.y + hourLength * sin(hourAngle).toFloat()
                ),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minute hand
            val minAngle = Math.toRadians((min * 6 - 90).toDouble())
            val minLength = radius * 0.72f
            drawLine(
                color = ChromeBright,
                start = center,
                end = Offset(
                    center.x + minLength * cos(minAngle).toFloat(),
                    center.y + minLength * sin(minAngle).toFloat()
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second hand (Neon accent)
            val secAngle = Math.toRadians((sec * 6 - 90).toDouble())
            val secLength = radius * 0.85f
            val secTail = radius * 0.18f
            drawLine(
                color = themeAccent.primary,
                start = Offset(
                    center.x - secTail * cos(secAngle).toFloat(),
                    center.y - secTail * sin(secAngle).toFloat()
                ),
                end = Offset(
                    center.x + secLength * cos(secAngle).toFloat(),
                    center.y + secLength * sin(secAngle).toFloat()
                ),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center pivot cap
            drawCircle(color = themeAccent.primary, radius = 4.dp.toPx())
            drawCircle(color = PitchBlack, radius = 1.8.dp.toPx())
        }

        // Center typography label
        Text(
            text = "CASSETTE",
            color = ChromeDark,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 60.dp)
        )
    }
}

@Composable
private fun NeonCyberClock(
    timeState: TimeState,
    themeAccent: ThemeAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF06060A))
            .border(1.dp, themeAccent.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CyberDigitBox(label = "HR", value = timeState.hour24, accent = themeAccent)
        Text(text = "::", color = themeAccent.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        CyberDigitBox(label = "MIN", value = timeState.minute, accent = themeAccent)
        Text(text = "::", color = themeAccent.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        CyberDigitBox(label = "SEC", value = timeState.second, accent = themeAccent)
    }
}

@Composable
private fun CyberDigitBox(
    label: String,
    value: String,
    accent: ThemeAccent
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = ChromeDark,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = accent.primary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ChromeStackClock(
    timeState: TimeState,
    themeAccent: ThemeAccent
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = timeState.hour12,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = ChromeHighlight,
                letterSpacing = (-2).sp,
                lineHeight = 58.sp
            )
            Text(
                text = "HOUR",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ChromeDark,
                letterSpacing = 2.sp
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .height(60.dp)
                .width(2.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, themeAccent.primary, Color.Transparent)
                    )
                )
        )

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = timeState.minute,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = themeAccent.primary,
                letterSpacing = (-2).sp,
                lineHeight = 58.sp
            )
            Text(
                text = "${timeState.second} SEC",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ChromeMid,
                letterSpacing = 2.sp
            )
        }
    }
}
