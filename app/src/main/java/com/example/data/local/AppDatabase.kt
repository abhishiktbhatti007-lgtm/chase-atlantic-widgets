package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "widget_settings")
data class WidgetSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val enabledWidgets: String = "CLOCK,NOW_PLAYING,AUDIO_VISUALIZER,LYRICS,COUNTDOWN,DATE_TRACKER,MOOD_ATMOSPHERE,ALBUM_SHOWCASE,MOOD_QUOTES,NIGHT_MODE",
    val clockStyle: String = "OVERSIZED_DIGITAL",
    val themeAccent: String = "CRIMSON",
    val ambientMode: String = "SMOKE_GLASS",
    val countdownTitle: String = "MIDNIGHT ALBUM DROP",
    val countdownTargetEpoch: Long = System.currentTimeMillis() + (18 * 3600 * 1000L) + (42 * 60 * 1000L)
)

@Entity(tableName = "saved_favorites")
data class FavoriteQuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quote: String,
    val source: String,
    val moodTag: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widget_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<WidgetSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: WidgetSettingsEntity)

    @Query("SELECT * FROM saved_favorites ORDER BY savedTimestamp DESC")
    fun getFavorites(): Flow<List<FavoriteQuoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(quote: FavoriteQuoteEntity)

    @Query("DELETE FROM saved_favorites WHERE id = :id")
    suspend fun removeFavorite(id: Long)
}

@Database(
    entities = [WidgetSettingsEntity::class, FavoriteQuoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetDao
}
