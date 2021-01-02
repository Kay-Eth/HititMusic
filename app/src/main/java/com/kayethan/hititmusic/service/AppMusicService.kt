package com.kayethan.hititmusic.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.provider.Settings;
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.kayethan.hititmusic.data.MusicFile

class AppMusicService : Service() {
    private val TAG: String = "HititMusicService"
    private var player: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service onCreate")
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "Service onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand " + startId)

        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
    }
}