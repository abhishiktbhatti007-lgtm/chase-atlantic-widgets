package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.service.MediaSyncManager

class SpotifyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            "com.spotify.music.metadatachanged",
            "com.spotify.music.playbackstatechanged",
            "com.spotify.music.queuechanged" -> {
                val track = intent.getStringExtra("track")
                val artist = intent.getStringExtra("artist")
                val album = intent.getStringExtra("album")
                val length = intent.getIntExtra("length", 0).toLong()
                val playing = intent.getBooleanExtra("playing", false)
                val position = intent.getIntExtra("playbackPosition", 0).toLong()

                MediaSyncManager.updateFromSpotifyBroadcast(
                    track = track,
                    artist = artist,
                    album = album,
                    lengthMs = length,
                    isPlaying = playing,
                    positionMs = position
                )
            }
        }
    }
}
