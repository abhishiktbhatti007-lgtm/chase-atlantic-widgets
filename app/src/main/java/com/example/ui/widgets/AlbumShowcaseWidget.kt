package com.example.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeAccent
import com.example.data.model.Track
import com.example.ui.components.GlassCard
import com.example.ui.components.WidgetHeader
import com.example.ui.theme.*

@Composable
fun AlbumShowcaseWidget(
    tracks: List<Track>,
    currentTrackId: String,
    themeAccent: ThemeAccent,
    onSelectTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("album_showcase_widget"),
        glowColor = themeAccent.glow
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WidgetHeader(
                tag = "08 // SHOWCASE",
                title = "Discography Archive",
                accentColor = themeAccent.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(tracks) { track ->
                    val isCurrent = track.id == currentTrackId
                    Column(
                        modifier = Modifier
                            .width(115.dp)
                            .clickable { onSelectTrack(track) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black)
                                .border(
                                    if (isCurrent) 2.dp else 1.dp,
                                    if (isCurrent) themeAccent.primary else Color(0x33FFFFFF),
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = track.coverRes),
                                contentDescription = track.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(themeAccent.primary)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PLAYING",
                                        color = Color.White,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = track.title,
                            color = if (isCurrent) themeAccent.primary else ChromeBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = track.album,
                            color = ChromeDark,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
