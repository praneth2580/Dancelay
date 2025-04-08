package com.mrsomething.daneplay.utils

import android.content.Context
import android.media.MediaPlayer

class MusicQueuePlayer(
    private val context: Context,
    private val audioList: List<AudioFile>
) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    fun play() {
        if (mediaPlayer == null) {
            preparePlayer(currentIndex)
        }
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun nextSong() {
        if (currentIndex < audioList.lastIndex) {
            currentIndex++
            preparePlayer(currentIndex)
            mediaPlayer?.start()
        }
    }

    fun prevSong() {
        if (currentIndex > 0) {
            currentIndex--
            preparePlayer(currentIndex)
            mediaPlayer?.start()
        }
    }

    fun skipForward(seconds: Int) {
        mediaPlayer?.let {
            val newPos = it.currentPosition + seconds * 1000
            it.seekTo(newPos.coerceAtMost(it.duration))
        }
    }

    fun skipBackward(seconds: Int) {
        mediaPlayer?.let {
            val newPos = it.currentPosition - seconds * 1000
            it.seekTo(newPos.coerceAtLeast(0))
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun preparePlayer(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, audioList[index].uri)
        mediaPlayer?.setOnCompletionListener {
            nextSong()
        }
    }
}