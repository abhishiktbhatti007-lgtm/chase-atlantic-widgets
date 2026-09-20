package com.example.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeAccent
import com.example.ui.components.GlassCard
import com.example.ui.components.WidgetHeader
import com.example.ui.theme.*

@Composable
fun VisualizerWidget(
    levels: List<Float>,
    waveformPoints: List<Float>,
    isPlaying: Boolean,
    themeAccent: ThemeAccent,
    modifier: Modifier = Modifier
) {
    var modeIsBars by remember { mutableStateOf(true) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("visualizer_widget"),
        glowColor = themeAccent.glow
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WidgetHeader(
                tag = "03 // FREQUENCY",
                title = if (modeIsBars) "EQ Spectrum (32-16kHz)" else "Analog Oscilloscope",
                accentColor = themeAccent.primary,
                action = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
                            .clickable { modeIsBars = !modeIsBars }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (modeIsBars) "MODE: BARS" else "MODE: WAVE",
                            color = themeAccent.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF07070B))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (modeIsBars) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val numBars = levels.size
                        val totalWidth = size.width
                        val totalHeight = size.height
                        val barSpacing = 4.dp.toPx()
                        val barWidth = (totalWidth - (numBars - 1) * barSpacing) / numBars

                        val barBrush = Brush.verticalGradient(
                            colors = listOf(
                                themeAccent.primary,
                                themeAccent.secondary,
                                Color(0x33000000)
                            )
                        )

                        for (i in 0 until numBars) {
                            val level = levels[i].coerceIn(0.06f, 1f)
                            val barHeight = totalHeight * level
                            val left = i * (barWidth + barSpacing)
                            val top = totalHeight - barHeight

                            // Main equalizer bar
                            drawRoundRect(
                                brush = barBrush,
                                topLeft = Offset(left, top),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            )

                            // Peak dot
                            val peakY = (top - 4.dp.toPx()).coerceAtLeast(0f)
                            drawRoundRect(
                                color = ChromeHighlight.copy(alpha = if (isPlaying) 0.9f else 0.4f),
                                topLeft = Offset(left, peakY),
                                size = Size(barWidth, 2.dp.toPx()),
                                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                            )
                        }
                    }
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val points = waveformPoints
                        val w = size.width
                        val h = size.height
                        val step = w / (points.size - 1)

                        val path = Path()
                        points.forEachIndexed { index, p ->
                            val x = index * step
                            val y = h * (1f - p)
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                val prevX = (index - 1) * step
                                val prevY = h * (1f - points[index - 1])
                                val cx = (prevX + x) / 2
                                path.quadraticBezierTo(prevX, prevY, cx, (prevY + y) / 2)
                            }
                        }

                        // Glow stroke
                        drawPath(
                            path = path,
                            color = themeAccent.primary.copy(alpha = 0.35f),
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Sharp stroke
                        drawPath(
                            path = path,
                            color = ChromeHighlight,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Frequency Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("32Hz", "125Hz", "500Hz", "2kHz", "8kHz", "16kHz").forEach { label ->
                    Text(
                        text = label,
                        color = ChromeDark,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
