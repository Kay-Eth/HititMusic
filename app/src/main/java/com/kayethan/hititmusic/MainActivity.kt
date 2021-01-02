package com.kayethan.hititmusic

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayethan.hititmusic.data.MusicFile
import com.kayethan.hititmusic.databinding.ActivityMainBinding
import com.kayethan.hititmusic.musiclist.MusicListFragment
import com.kayethan.hititmusic.player.PlayerFragment
import com.kayethan.hititmusic.service.AppMusicService
import com.kayethan.hititmusic.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE = 1

        lateinit var mediaPlayer: MediaPlayer

        fun getTotalTime(): Int {
            return mediaPlayer.duration
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_list -> {
                loadFragment(MusicListFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_player -> {
                loadFragment(PlayerFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                loadFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_player
        }

        val intent = Intent(this, AppMusicService::class.java)
        startService(intent)

        val musicFiles = getAllMusic()

        for (file in musicFiles) {
            Log.i("TEST", file.path + " | " + file.title)
        }

        // TEMP
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
        mediaPlayer.setDataSource(applicationContext, ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicFiles[0].id))
        mediaPlayer.prepare()
        mediaPlayer.start()
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE)
        } else {
            Log.i("permission", "Already granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "Granted")
            }
            else
            {
                ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE)
            }
        }
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
}