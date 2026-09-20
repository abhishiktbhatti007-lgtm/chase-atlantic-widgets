package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun NightModeWidget(
    isNightModeOnly: Boolean,
    themeAccent: ThemeAccent,
    onToggleNightMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("night_mode_widget"),
        glowColor = themeAccent.glow,
        onClick = onToggleNightMode
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WidgetHeader(
                tag = "10 // DISPLAY",
                title = "AMOLED Canvas",
                accentColor = themeAccent.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF07070B))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isNightModeOnly) themeAccent.primary else Color(0x44FFFFFF))
                    )

                    Column {
                        Text(
                            text = if (isNightModeOnly) "ULTRA-DARK AMOLED ACTIVE" else "LIQUID GLASS THEME ACTIVE",
                            color = ChromeHighlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isNightModeOnly) "Pure 0% sub-pixel black canvas for OLED displays" else "Moody dark glass with chrome reflections",
                            color = ChromeDark,
                            fontSize = 10.sp
                        )
                    }
                }

                Switch(
                    checked = isNightModeOnly,
                    onCheckedChange = { onToggleNightMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = themeAccent.primary,
                        uncheckedThumbColor = ChromeMid,
                        uncheckedTrackColor = Color(0x33FFFFFF)
                    )
                )
            }
        }
    }
}
