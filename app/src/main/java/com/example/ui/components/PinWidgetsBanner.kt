package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeAccent
import com.example.receiver.WidgetPinHelper
import com.example.ui.theme.ChromeDark
import com.example.ui.theme.ChromeHighlight
import com.example.ui.theme.ChromeMid

@Composable
fun PinWidgetsBanner(
    themeAccent: ThemeAccent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPinSupported = WidgetPinHelper.isPinSupported(context)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pin_widgets_banner"),
        glowColor = themeAccent.glow
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WidgetHeader(
                tag = "ANDROID HOME SCREEN",
                title = "Add Widgets to Phone",
                accentColor = themeAccent.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pin these dark luxury widgets directly to your Android launcher:",
                color = ChromeMid,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Quick Pin Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PinWidgetButton(
                    title = "CLOCK",
                    icon = Icons.Filled.Schedule,
                    themeAccent = themeAccent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isPinSupported) {
                            val success = WidgetPinHelper.pinClockWidget(context)
                            if (!success) {
                                Toast.makeText(context, "Long-press home screen -> Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Long-press your Home Screen -> Tap Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                PinWidgetButton(
                    title = "PLAYER",
                    icon = Icons.Filled.MusicNote,
                    themeAccent = themeAccent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isPinSupported) {
                            val success = WidgetPinHelper.pinPlayerWidget(context)
                            if (!success) {
                                Toast.makeText(context, "Long-press home screen -> Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Long-press your Home Screen -> Tap Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                PinWidgetButton(
                    title = "QUOTE",
                    icon = Icons.Filled.FormatQuote,
                    themeAccent = themeAccent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isPinSupported) {
                            val success = WidgetPinHelper.pinQuoteWidget(context)
                            if (!success) {
                                Toast.makeText(context, "Long-press home screen -> Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Long-press your Home Screen -> Tap Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Helper instruction
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF07070B))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Tip: You can also long-press any empty space on your phone's Home Screen -> tap 'Widgets' -> find 'Cassette Atlantic' and drag to place.",
                    color = ChromeDark,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun PinWidgetButton(
    title: String,
    icon: ImageVector,
    themeAccent: ThemeAccent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF07070B))
            .border(1.dp, themeAccent.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = themeAccent.primary,
                    modifier = Modifier.size(12.dp)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeAccent.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                color = ChromeHighlight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}
