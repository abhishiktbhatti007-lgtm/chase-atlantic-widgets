package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeAccent
import com.example.ui.components.GlassCard
import com.example.ui.components.WidgetHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.CountdownRemaining

@Composable
fun CountdownWidget(
    title: String,
    remaining: CountdownRemaining,
    themeAccent: ThemeAccent,
    onSelectPreset: (String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("countdown_widget"),
        glowColor = themeAccent.glow
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WidgetHeader(
                tag = "05 // COUNTDOWN",
                title = title,
                accentColor = themeAccent.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Neon Digits Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DigitBlock(label = "DAYS", value = String.format("%02d", remaining.days), accent = themeAccent)
                Text(text = ":", color = themeAccent.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                DigitBlock(label = "HOURS", value = String.format("%02d", remaining.hours), accent = themeAccent)
                Text(text = ":", color = themeAccent.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                DigitBlock(label = "MINS", value = String.format("%02d", remaining.minutes), accent = themeAccent)
                Text(text = ":", color = themeAccent.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                DigitBlock(label = "SECS", value = String.format("%02d", remaining.seconds), accent = themeAccent)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "MIDNIGHT DROP" to (6 * 3600 * 1000L),
                    "WORLD TOUR" to (14 * 86400 * 1000L),
                    "VIP CASSETTE" to (3 * 86400 * 1000L)
                ).forEach { (presetTitle, duration) ->
                    val isSelected = title == presetTitle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) themeAccent.primary.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                            .border(
                                1.dp,
                                if (isSelected) themeAccent.primary else Color(0x1AFFFFFF),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onSelectPreset(presetTitle, System.currentTimeMillis() + duration)
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = presetTitle,
                            color = if (isSelected) themeAccent.primary else ChromeMid,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DigitBlock(
    label: String,
    value: String,
    accent: ThemeAccent
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF07070B))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = accent.primary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = ChromeDark,
            letterSpacing = 1.sp
        )
    }
}
