package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ThemeAccent
import com.example.receiver.WidgetPinHelper
import com.example.receiver.WidgetSettingsManager
import com.example.ui.theme.ChromeDark
import com.example.ui.theme.ChromeHighlight
import com.example.ui.theme.ChromeMid

data class WidgetDescriptor(
    val id: String,
    val title: String,
    val subtitle: String,
    val sizeBadge: String,
    val icon: ImageVector,
    val previewImageRes: Int? = null,
    val pinAction: (android.content.Context) -> Boolean
)

@Composable
fun PinWidgetsBanner(
    themeAccent: ThemeAccent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPinSupported = WidgetPinHelper.isPinSupported(context)

    var showCustomizer by remember { mutableStateOf(false) }

    // Read current widget settings
    var selectedAccentName by remember { mutableStateOf(WidgetSettingsManager.getAccentName(context)) }
    var selectedAccentColor by remember { mutableStateOf(WidgetSettingsManager.getAccentColor(context)) }
    var selectedBgStyle by remember { mutableStateOf(WidgetSettingsManager.getBgStyle(context)) }
    var is24Hour by remember { mutableStateOf(WidgetSettingsManager.is24Hour(context)) }
    var showSeconds by remember { mutableStateOf(WidgetSettingsManager.isShowSeconds(context)) }

    val allAccents = remember {
        listOf(
            "Deep Crimson" to 0xFFFF1E56.toInt(),
            "Neon Violet" to 0xFFB026FF.toInt(),
            "Liquid Chrome" to 0xFFE0E0EB.toInt(),
            "Electric Cyan" to 0xFF00E5FF.toInt(),
            "Neon Emerald" to 0xFF00FF87.toInt(),
            "Noir Gold" to 0xFFFFB300.toInt(),
            "Hot Pink" to 0xFFFF007F.toInt()
        )
    }

    val availableWidgets = remember {
        listOf(
            WidgetDescriptor(
                id = "clock",
                title = "Minimalist Clock",
                subtitle = "Oversized typography with date",
                sizeBadge = "4×2",
                icon = Icons.Filled.Schedule,
                pinAction = { WidgetPinHelper.pinClockWidget(it) }
            ),
            WidgetDescriptor(
                id = "analog_3d",
                title = "3D Chrono Dial",
                subtitle = "Luxury dark metallic dial",
                sizeBadge = "2×2",
                icon = Icons.Filled.WatchLater,
                previewImageRes = R.drawable.img_3d_analog_dial,
                pinAction = { WidgetPinHelper.pinAnalog3DWidget(it) }
            ),
            WidgetDescriptor(
                id = "cassette_3d",
                title = "3D Cassette Deck",
                subtitle = "Floating magnetic tape & LED",
                sizeBadge = "4×2",
                icon = Icons.Filled.PlayCircle,
                previewImageRes = R.drawable.img_3d_cassette_art,
                pinAction = { WidgetPinHelper.pinCassette3DWidget(it) }
            ),
            WidgetDescriptor(
                id = "player",
                title = "Spotify Music Deck",
                subtitle = "Now Playing & interactive controls",
                sizeBadge = "4×2",
                icon = Icons.Filled.MusicNote,
                pinAction = { WidgetPinHelper.pinPlayerWidget(it) }
            ),
            WidgetDescriptor(
                id = "countdown",
                title = "Drop Countdown",
                subtitle = "Segmented tour radar timer",
                sizeBadge = "4×2",
                icon = Icons.Filled.Timer,
                pinAction = { WidgetPinHelper.pinCountdownWidget(it) }
            ),
            WidgetDescriptor(
                id = "horizon",
                title = "Night Horizon",
                subtitle = "Moon phase & sunrise countdown",
                sizeBadge = "4×2",
                icon = Icons.Filled.Nightlight,
                pinAction = { WidgetPinHelper.pinHorizonWidget(it) }
            ),
            WidgetDescriptor(
                id = "quote",
                title = "Mood Quotes",
                subtitle = "Aesthetic Chase Atlantic lyrics",
                sizeBadge = "4×1",
                icon = Icons.Filled.FormatQuote,
                pinAction = { WidgetPinHelper.pinQuoteWidget(it) }
            )
        )
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pin_widgets_banner"),
        glowColor = Color(selectedAccentColor).copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WidgetHeader(
                    tag = "ANDROID WIDGET STUDIO",
                    title = "7 Custom Widgets",
                    accentColor = Color(selectedAccentColor)
                )

                // Customize Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF14151E))
                        .border(1.dp, Color(selectedAccentColor).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showCustomizer = !showCustomizer }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (showCustomizer) Icons.Filled.Close else Icons.Filled.Palette,
                            contentDescription = "Customize",
                            tint = Color(selectedAccentColor),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (showCustomizer) "HIDE" else "THEME",
                            color = ChromeHighlight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose from 7 dark luxury 3D and minimalist widgets. Customize colors and pin directly to your phone launcher:",
                color = ChromeMid,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            // CUSTOMIZATION EXPANDER
            AnimatedVisibility(visible = showCustomizer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF07070B))
                        .border(1.dp, Color(selectedAccentColor).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "● WIDGET ACCENT COLOR",
                        color = Color(selectedAccentColor),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Color swatch row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        allAccents.forEach { (name, colorInt) ->
                            val isSelected = selectedAccentColor == colorInt
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorInt))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) ChromeHighlight else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedAccentColor = colorInt
                                        selectedAccentName = name
                                        WidgetSettingsManager.setAccent(context, colorInt, name)
                                        Toast.makeText(context, "Accent applied: $name", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = if (colorInt == 0xFFE0E0EB.toInt()) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "● WIDGET GLASS TEXTURE",
                        color = Color(selectedAccentColor),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val bgOptions = listOf(
                            "Obsidian" to WidgetSettingsManager.BG_OBSIDIAN_GLASS,
                            "Pure AMOLED" to WidgetSettingsManager.BG_AMOLED_BLACK,
                            "Carbon" to WidgetSettingsManager.BG_SMOKED_CARBON
                        )

                        bgOptions.forEach { (label, styleCode) ->
                            val isSelected = selectedBgStyle == styleCode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF1F202B) else Color(0xFF0F1017))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(selectedAccentColor) else Color(0x33FFFFFF),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedBgStyle = styleCode
                                        WidgetSettingsManager.setBgStyle(context, styleCode)
                                        Toast.makeText(context, "Background style updated: $label", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) ChromeHighlight else ChromeMid,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clock format toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "24-Hour Military Format",
                            color = ChromeMid,
                            fontSize = 11.sp
                        )
                        Switch(
                            checked = is24Hour,
                            onCheckedChange = {
                                is24Hour = it
                                WidgetSettingsManager.set24Hour(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(selectedAccentColor),
                                checkedTrackColor = Color(selectedAccentColor).copy(alpha = 0.3f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Show Seconds Indicator",
                            color = ChromeMid,
                            fontSize = 11.sp
                        )
                        Switch(
                            checked = showSeconds,
                            onCheckedChange = {
                                showSeconds = it
                                WidgetSettingsManager.setShowSeconds(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(selectedAccentColor),
                                checkedTrackColor = Color(selectedAccentColor).copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            WidgetSettingsManager.updateAllWidgets(context)
                            Toast.makeText(context, "All home screen widgets refreshed!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(selectedAccentColor)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYNC TO HOME SCREEN NOW",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LIST OF ALL 7 WIDGETS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableWidgets.forEach { item ->
                    WidgetRowCard(
                        descriptor = item,
                        accentColor = Color(selectedAccentColor),
                        isPinSupported = isPinSupported,
                        onPin = {
                            if (isPinSupported) {
                                val success = item.pinAction(context)
                                if (!success) {
                                    Toast.makeText(context, "Long-press home screen -> Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Long-press your Home Screen -> Tap Widgets -> Cassette Atlantic", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
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
                    text = "Tip: You can also long-press any empty space on your phone's Home Screen -> tap 'Widgets' -> find 'Cassette Atlantic' to place any of the 7 widgets.",
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
private fun WidgetRowCard(
    descriptor: WidgetDescriptor,
    accentColor: Color,
    isPinSupported: Boolean,
    onPin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0B10))
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail / Icon
            if (descriptor.previewImageRes != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF14151E))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = painterResource(id = descriptor.previewImageRes),
                        contentDescription = descriptor.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF14151E))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = descriptor.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = descriptor.title,
                        color = ChromeHighlight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1C1D29))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = descriptor.sizeBadge,
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descriptor.subtitle,
                    color = ChromeMid,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Pin Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14151E))
                    .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onPin)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "ADD",
                        color = ChromeHighlight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
