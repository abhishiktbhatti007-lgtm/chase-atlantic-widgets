package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteQuoteEntity
import com.example.data.local.WidgetSettingsEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository private constructor(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "cassette_atlantic.db"
    ).build()

    private val dao = db.widgetDao()

    fun getSettings(): Flow<WidgetSettingsEntity?> = dao.getSettings()

    suspend fun saveSettings(settings: WidgetSettingsEntity) = dao.saveSettings(settings)

    fun getFavorites(): Flow<List<FavoriteQuoteEntity>> = dao.getFavorites()

    suspend fun saveFavoriteQuote(quote: QuoteItem) {
        dao.addFavorite(
            FavoriteQuoteEntity(
                quote = quote.quote,
                source = quote.source,
                moodTag = quote.moodTag
            )
        )
    }

    suspend fun removeFavorite(id: Long) = dao.removeFavorite(id)

    val sampleTracks: List<Track> = listOf(
        Track(
            id = "t1",
            title = "SWIM",
            artist = "CHASE ATLANTIC",
            album = "Chase Atlantic (Deluxe)",
            coverRes = R.drawable.img_album_beauty,
            durationSeconds = 228,
            soundProfileVibe = "Dark Alt-R&B / 808 Trap",
            lyrics = listOf(
                LyricLine(0, "Baby, take a deep breath..."),
                LyricLine(4000, "Falling underneath the deep red lights"),
                LyricLine(9000, "I see the chrome reflection in your eyes"),
                LyricLine(14000, "Can you feel the 808 inside your chest?"),
                LyricLine(19000, "Swim with the devil 'til the water turns cold"),
                LyricLine(24000, "Too many secrets that were never told"),
                LyricLine(29000, "Speeding through the midnight boulevard"),
                LyricLine(35000, "Luxury and smoke, we tore it all apart"),
                LyricLine(41000, "No sleep tonight, no looking back"),
                LyricLine(48000, "Lost inside the frequency of black")
            )
        ),
        Track(
            id = "t2",
            title = "BEAUTY IN DEATH",
            artist = "CHASE ATLANTIC",
            album = "BEAUTY IN DEATH",
            coverRes = R.drawable.img_album_phases,
            durationSeconds = 204,
            soundProfileVibe = "Moody Alt-Pop / Rock Synth",
            lyrics = listOf(
                LyricLine(0, "Static on the radio dial..."),
                LyricLine(5000, "Two-door coupe in the pouring rain"),
                LyricLine(11000, "Trying to numb this beautiful pain"),
                LyricLine(17000, "Obsidian glass and a crimson ring"),
                LyricLine(23000, "Listen to the dark chorus sing"),
                LyricLine(30000, "There is beauty in the death of what we knew"),
                LyricLine(36000, "Leaving tire marks in shades of neon blue"),
                LyricLine(43000, "Say it's fine when nobody's around"),
                LyricLine(50000, "Drown the silence with a louder sound")
            )
        ),
        Track(
            id = "t3",
            title = "SLOW DOWN",
            artist = "CHASE ATLANTIC",
            album = "Phases",
            coverRes = R.drawable.img_app_icon,
            durationSeconds = 236,
            soundProfileVibe = "Liquid Synth / Atmospheric Trap",
            lyrics = listOf(
                LyricLine(0, "Everything is moving way too fast..."),
                LyricLine(6000, "Slow down, baby take it slow"),
                LyricLine(12000, "You're the only high I wanna know"),
                LyricLine(18000, "Cold leather seats, windows fogged in steam"),
                LyricLine(25000, "Living inside an endless midnight dream"),
                LyricLine(32000, "Don't let go until the sun comes up"),
                LyricLine(39000, "Pour another drop inside the cup"),
                LyricLine(46000, "Keep the tempo slow, let the bassline roll")
            )
        ),
        Track(
            id = "t4",
            title = "CHROME HEARTS",
            artist = "CHASE ATLANTIC",
            album = "Lost In Paradise",
            coverRes = R.drawable.img_album_beauty,
            durationSeconds = 198,
            soundProfileVibe = "Guitar Noir / Dark Trap",
            lyrics = listOf(
                LyricLine(0, "Guitar strums echoing in the hall..."),
                LyricLine(5000, "Chrome hearts dangling from the rear mirror"),
                LyricLine(10000, "Objects in the dark are closer and clearer"),
                LyricLine(16000, "We burn out like a shooting flare"),
                LyricLine(22000, "Late night adrenaline in the air"),
                LyricLine(28000, "Cold touch, silver chains, zero regrets"),
                LyricLine(35000, "Underneath the city silhouette")
            )
        )
    )

    val moodQuotes: List<QuoteItem> = listOf(
        QuoteItem("q1", "Too late to sleep, too early to think.", "CHASE ATLANTIC // MOOD", "3:14 AM"),
        QuoteItem("q2", "Lost in the smoke between midnight and sunrise.", "CASSETTE NOIR", "LATE NIGHT"),
        QuoteItem("q3", "Fast cars, cold hands, chrome hearts.", "BEAUTY IN DEATH", "AESTHETIC"),
        QuoteItem("q4", "Swim with the devil until the water turns cold.", "ATLANTIC ARCHIVE", "LYRIC"),
        QuoteItem("q5", "Beauty in death, luxury in silence.", "NOIR VIBE", "3:00 AM"),
        QuoteItem("q6", "Drown the quiet with a heavy bassline.", "SOUNDWAVE", "AUDIO"),
        QuoteItem("q7", "Neon reflections on wet asphalt tell no lies.", "PHASES", "CITY NOIR"),
        QuoteItem("q8", "Some souls only feel alive after 2 AM.", "CASSETTE LOG", "VIBE")
    )

    val soundscapes: List<SoundscapeItem> = listOf(
        SoundscapeItem("s1", "Rain On Glass", "water_drop", "432 Hz Ambient"),
        SoundscapeItem("s2", "808 Night Pulse", "graphic_eq", "60 Hz Low End"),
        SoundscapeItem("s3", "Vinyl Crackle", "album", "Lo-Fi Texture"),
        SoundscapeItem("s4", "Neon Haze", "blur_on", "Midnight Resonance")
    )

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context).also { INSTANCE = it }
            }
        }
    }
}
