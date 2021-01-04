package com.georgiyshur.heartask.model.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.georgiyshur.heartask.model.Song
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * Service responsible for playing the audio on background. It handles media session and sticky
 * notification.
 */
class AudioPlaybackService : LifecycleService() {

    companion object {

        fun newIntent(context: Context) = Intent(context, AudioPlaybackService::class.java)
    }

    inner class AudioServiceBinder : Binder() {

        val service get() = this@AudioPlaybackService
    }

    private lateinit var playerDelegate: PlayerDelegate

    val playerStateLiveData get() = playerDelegate.playerStateLiveData

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return AudioServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        playerDelegate = PlayerDelegate(applicationContext, NotificationListener())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerDelegate.stop()
    }

    fun play(song: Song) {
        playerDelegate.play(song)
    }

    fun pause() {
        playerDelegate.pause()
    }

    fun rewind() {
        playerDelegate.rewind()
    }

    fun forward() {
        playerDelegate.forward()
    }

    inner class NotificationListener : PlayerNotificationManager.NotificationListener {

        override fun onNotificationStarted(notificationId: Int, notification: Notification) {
            startForeground(notificationId, notification)
        }

        override fun onNotificationCancelled(notificationId: Int) {
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing) {
                // Make sure the service will not get destroyed while playing media.
                startForeground(notificationId, notification)
            } else {
                // Make notification cancellable.
                stopForeground(false)
            }
        }
    }
}