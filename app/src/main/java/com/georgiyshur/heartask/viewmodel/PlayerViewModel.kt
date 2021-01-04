package com.georgiyshur.heartask.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.*
import com.georgiyshur.heartask.model.PlayerState
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.model.service.AudioPlaybackService

/**
 * View-model that handles player state in presentation.
 */
class PlayerViewModel : ViewModel() {

    /*
    Generally it's not a good practice to have Android components in view-model, but here it's kinda
    OK for the sake of simplicity and because the service will never outlive the VM.
     */
    private var audioPlaybackService: AudioPlaybackService? = null

    private val _playerStateLiveData = MediatorLiveData<PlayerState>()
    val playerStateLiveData: LiveData<PlayerState> = _playerStateLiveData.distinctUntilChanged()

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlaybackService.AudioServiceBinder
            audioPlaybackService = binder.service
            _playerStateLiveData.addSource(audioPlaybackService!!.playerStateLiveData) { playState ->
                _playerStateLiveData.postValue(playState)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _playerStateLiveData.removeSource(audioPlaybackService!!.playerStateLiveData)
            audioPlaybackService = null
        }
    }

    fun play(song: Song) {
        audioPlaybackService?.play(song)
    }

    fun pause() {
        audioPlaybackService?.pause()
    }

    fun rewind() {
        audioPlaybackService?.rewind()
    }

    fun forward() {
        audioPlaybackService?.forward()
    }
}