package com.kayethan.hititmusic

import android.content.*
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
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
import com.kayethan.hititmusic.service.HititMusicService
import com.kayethan.hititmusic.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private var musicService: HititMusicService? = null
        private var bound: Boolean = false

        const val REQUEST_CODE_STORAGE = 1
        const val REQUEST_CODE_SERVICE = 2

//        lateinit var mediaPlayer: MediaPlayer
//
//        fun getTotalTime(): Int {
//            return mediaPlayer.duration
//        }

        fun getService(): HititMusicService? {
            return musicService
        }

        fun isServiceBound(): Boolean {
            return bound
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HititMusicService.HititMusicBinder
            musicService = binder.getService()
            bound = true
            Log.i("MainActivity", "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName?) {
            musicService = null
            bound = false
            Log.i("MainActivity", "onServiceDisconnected")
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

        Log.i("Main", "onCreate")
        Intent(this, HititMusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

//        val musicFiles = getAllMusic()
//
//        for (file in musicFiles) {
//            Log.i("TEST", file.path + " | " + file.title)
//        }

//        // TEMP
//        mediaPlayer = MediaPlayer()
//        mediaPlayer.setAudioAttributes(
//                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build()
//        )
//        mediaPlayer.setDataSource(applicationContext, ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicFiles[0].id))
//        mediaPlayer.prepare()
//        mediaPlayer.start()
//        mediaPlayer.isLooping = true
//        mediaPlayer.setVolume(0.5f, 0.5f)
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(connection)
        bound = false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_STORAGE)
        } else {
            Log.i("permission", "Already granted - READ_EXTERNAL_STORAGE")
        }

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.FOREGROUND_SERVICE }, REQUEST_CODE_SERVICE)
        } else {
            Log.i("permission", "Already granted - FOREGROUND_SERVICE")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "Granted")
            }
            else
            {
                ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_STORAGE)
            }
        }
        else if (requestCode == REQUEST_CODE_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "Granted")
            }
            else
            {
                ActivityCompat.requestPermissions(this@MainActivity, Array<String>(1) { android.Manifest.permission.FOREGROUND_SERVICE }, REQUEST_CODE_SERVICE)
            }
        }
    }

//    fun getAllMusic(): ArrayList<MusicFile> {
//        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val songCursor = contentResolver.query(songUri, null, null, null, null)
//        val result = ArrayList<MusicFile>()
//
//        if (songCursor != null && songCursor.moveToFirst()) {
//            val songId: Int = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val songTitle: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val songArtist: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//            val songAlbum: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
//            val songDuration: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
//            val songLocation: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
//
//            do {
//                val currentId: Long = songCursor.getLong(songId)
//                val currentTitle: String = songCursor.getString(songTitle)
//                val currentArtist: String = songCursor.getString(songArtist)
//                val currentLocation: String = songCursor.getString(songLocation)
//                val currentDuration: Int = songCursor.getInt(songDuration)
//                val currentAlbum: String = songCursor.getString(songAlbum)
//
//                if (currentLocation.endsWith(".mp3"))
//                    result.add(MusicFile(currentId, currentLocation, currentTitle, currentArtist, currentAlbum, currentDuration))
//
//            } while (songCursor.moveToNext())
//        }
//
//        return result
//    }
}