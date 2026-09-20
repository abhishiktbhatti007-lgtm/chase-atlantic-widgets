package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
import com.example.data.model.AmbientMode
import com.example.data.model.ClockStyle
import com.example.data.model.ThemeAccent
import com.example.data.model.WidgetType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCustomizeSheet(
    enabledWidgets: Set<WidgetType>,
    clockStyle: ClockStyle,
    themeAccent: ThemeAccent,
    ambientMode: AmbientMode,
    onToggleWidget: (WidgetType) -> Unit,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectThemeAccent: (ThemeAccent) -> Unit,
    onSelectAmbientMode: (AmbientMode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PitchBlack,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x44FFFFFF))
            )
        },
        modifier = Modifier.testTag("quick_customize_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WIDGET CONFIG",
                        color = themeAccent.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Customize Hub",
                        color = ChromeHighlight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = ChromeMid
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Accent Color Palette
                item {
                    Text(
                        text = "ACCENT COLOR",
                        color = ChromeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeAccent.values().forEach { accent ->
                            val isSelected = accent == themeAccent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF08080E))
                                    .border(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) accent.primary else Color(0x22FFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectThemeAccent(accent) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(accent.primary)
                                    )
                                    Text(
                                        text = accent.name,
                                        color = if (isSelected) ChromeHighlight else ChromeMid,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Clock Face Selector
                item {
                    Text(
                        text = "CLOCK FACE",
                        color = ChromeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ClockStyle.values().forEach { style ->
                            val isSelected = style == clockStyle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) themeAccent.primary.copy(alpha = 0.2f) else Color(0xFF08080E))
                                    .border(
                                        1.dp,
                                        if (isSelected) themeAccent.primary else Color(0x1AFFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectClockStyle(style) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = style.displayName,
                                    color = if (isSelected) ChromeHighlight else ChromeBright,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (isSelected) {
                                    Text(
                                        text = "SELECTED",
                                        color = themeAccent.primary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Ambient Wallpaper Mode
                item {
                    Text(
                        text = "AMBIENT CANVAS",
                        color = ChromeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AmbientMode.values().forEach { mode ->
                            val isSelected = mode == ambientMode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) themeAccent.primary.copy(alpha = 0.2f) else Color(0xFF08080E))
                                    .border(
                                        1.dp,
                                        if (isSelected) themeAccent.primary else Color(0x1AFFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectAmbientMode(mode) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.displayName.split(" ").first(),
                                    color = if (isSelected) themeAccent.primary else ChromeMid,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // 4. Widget Visibility Toggles
                item {
                    Text(
                        text = "ACTIVE WIDGETS",
                        color = ChromeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                }

                items(WidgetType.values()) { type ->
                    val isEnabled = enabledWidgets.contains(type)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF07070B))
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                            .clickable { onToggleWidget(type) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = type.title,
                                color = if (isEnabled) ChromeHighlight else ChromeDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = type.subtitle,
                                color = ChromeDark,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { onToggleWidget(type) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = themeAccent.primary,
                                uncheckedThumbColor = ChromeMid,
                                uncheckedTrackColor = Color(0x22FFFFFF)
                            )
                        )
                    }
                }
            }
        }
    }
}
