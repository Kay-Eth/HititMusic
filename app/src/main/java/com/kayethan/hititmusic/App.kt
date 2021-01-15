package com.kayethan.hititmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    companion object {
        const val CHANNEL_ID = "hititMusicServiceChannel"
        const val MUSIC_SERVICE_ACTION_START = "com.kayethan.hititmusic.player.start"
        const val MUSIC_SERVICE_ACTION_PLAY_PAUSE = "com.kayethan.hititmusic.player.play_pause"
        const val MUSIC_SERVICE_ACTION_STOP = "com.kayethan.hititmusic.player.stop"
        const val MUSIC_SERVICE_ACTION_NEXT = "com.kayethan.hititmusic.player.next"
        const val MUSIC_SERVICE_ACTION_PREVIOUS = "com.kayethan.hititmusic.player.previous"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Hitit Music Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}