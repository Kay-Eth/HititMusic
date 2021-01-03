package com.kayethan.hititmusic.service

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.widget.Toast
import androidx.media.MediaBrowserServiceCompat
import com.kayethan.hititmusic.MainActivity
import com.kayethan.hititmusic.R
import com.kayethan.hititmusic.data.MusicFile
import java.util.*

class HititMusicService : Service() {

    inner class HititMusicBinder : Binder() {
        fun getService(): HititMusicService = this@HititMusicService
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var musicFiles: ArrayList<MusicFile> = ArrayList<MusicFile>()
    private var currentIndex: Int = 0

    private val binder = HititMusicBinder()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("Service", "Service onCreate")

        musicFiles = getAllMusic()

        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener { this@HititMusicService.onSongEnd() }
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
        if (musicFiles.size > 0) {
            setMusicDataSource(musicFiles[currentIndex])
        } else {
            Toast.makeText(applicationContext, R.string.no_music_found, Toast.LENGTH_LONG).show()
        }

        mediaPlayer.setVolume(1f, 1f)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun getAllMusic(): ArrayList<MusicFile> {
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null, null)
        val result = ArrayList<MusicFile>()

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId: Int = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songAlbum: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val songDuration: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val songLocation: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val currentId: Long = songCursor.getLong(songId)
                val currentTitle: String = songCursor.getString(songTitle)
                val currentArtist: String = songCursor.getString(songArtist)
                val currentLocation: String = songCursor.getString(songLocation)
                val currentDuration: Int = songCursor.getInt(songDuration)
                val currentAlbum: String = songCursor.getString(songAlbum)

                if (currentLocation.endsWith(".mp3"))
                    result.add(MusicFile(currentId, currentLocation, currentTitle, currentArtist, currentAlbum, currentDuration))

            } while (songCursor.moveToNext())
        }

        return result
    }

    fun isPlaying(): Boolean {
        if (musicFiles.size == 0)
            return false
        return mediaPlayer.isPlaying
    }

    fun getDuration(): Int {
        if (musicFiles.size == 0)
            return 0
        return mediaPlayer.duration
    }

    fun play() {
        if (musicFiles.size == 0)
            return
        mediaPlayer.start()
    }

    fun pause() {
        if (musicFiles.size == 0)
            return
        mediaPlayer.pause()
    }

    fun seekTo(seconds: Int) {
        if (musicFiles.size == 0)
            return
        mediaPlayer.seekTo(seconds)
    }

    fun getCurrentPosition(): Int {
        if (musicFiles.size == 0)
            return 0
        return mediaPlayer.currentPosition
    }

    fun getCurrentSong(): MusicFile? {
        if (musicFiles.size == 0)
            return null
        return musicFiles[currentIndex]
    }

    fun nextSong() {
        if (musicFiles.size == 0)
            return

        currentIndex++

        if (currentIndex >= musicFiles.count()) {
            currentIndex = 0
        }

        playMusic(currentIndex)
    }

    fun previousSong() {
        if (musicFiles.size == 0)
            return

        if (mediaPlayer.currentPosition > 5000)
            mediaPlayer.seekTo(0)
        else
        {
            currentIndex--

            if (currentIndex < 0) {
                currentIndex = musicFiles.count() - 1
            }

            playMusic(currentIndex)
        }
    }

    fun playMusic(idx: Int, reset: Boolean = true) {
        if (musicFiles.size == 0)
            return

        if (idx < 0 || idx >= musicFiles.count())
            return

        currentIndex = idx
        if (reset)
            mediaPlayer.reset()
        setMusicDataSource(musicFiles[idx])
        play()
    }

    private fun setMusicDataSource(musicFile: MusicFile) {
        if (musicFiles.size == 0)
            return

        mediaPlayer.setDataSource(applicationContext, ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicFile.id))
        mediaPlayer.prepare()
    }

    private fun onSongEnd() {
        if (musicFiles.size == 0)
            return

        nextSong()
    }
}