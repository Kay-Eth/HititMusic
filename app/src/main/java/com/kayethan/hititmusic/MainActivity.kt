package com.kayethan.hititmusic

import android.content.*
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayethan.hititmusic.data.MusicFile
import com.kayethan.hititmusic.databinding.ActivityMainBinding
import com.kayethan.hititmusic.musiclist.MusicListFragment
import com.kayethan.hititmusic.player.PlayerFragment
import com.kayethan.hititmusic.service.HititMusicService
import com.kayethan.hititmusic.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private lateinit var MA: MainActivity
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

        fun changeToPlayer() {
            MA.bottomNavigationView.selectedItemId = R.id.navigation_player
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
        MA = this

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        if (savedInstanceState == null)
            bottomNavigationView.selectedItemId = R.id.navigation_player


        if (musicService == null) {
            Log.i("Main", "onCreate")
            Intent(this, HititMusicService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }

            val intent = Intent(this, HititMusicService::class.java)
                    .setAction(App.MUSIC_SERVICE_ACTION_START)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }else{
                startService(intent);
            }
        }

        Intent(this, HititMusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
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
}