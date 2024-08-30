package com.shermanrex.recorderApp.data.service.connection

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.shermanrex.recorderApp.data.mapper.toMediaItem
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.data.service.MediaPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaPlayerServiceConnection @Inject constructor(var context: Context) {

  private var controllerFuture: ListenableFuture<MediaController>? = null
  private var mediaController: MediaController? = null
  private lateinit var playerStateListener: Player.Listener

  init {
    initialListener()
  }

  private var _mediaPlayerState = MutableStateFlow(CurrentMediaPlayerState())
  var mediaPlayerState = _mediaPlayerState.asStateFlow()

  var currentPosition = flow {
    while (true) {
      delay(50L)
      if (mediaController?.isPlaying == true) {
        emit(mediaController?.currentPosition)
      }
    }
  }

  fun startPlayAudio(recordModel: RecordModel) {
    mediaController?.setMediaItem(recordModel.toMediaItem())
    mediaController?.prepare()
    mediaController?.playWhenReady
    mediaController?.play()
  }

  fun stopPlayAudio() {
    mediaController?.stop()
    mediaController?.clearMediaItems()
  }

  fun resumeAudio() = mediaController?.play()

  fun pauseAudio() = mediaController?.pause()

  fun fastForwardAudio() =
    mediaController?.seekTo(mediaController?.currentPosition?.plus(15000L) ?: 0L)

  fun backForwardAudio() =
    mediaController?.seekTo(mediaController?.currentPosition?.minus(15000L) ?: 0L)

  fun seekToPosition(position: Float) {
    mediaController?.seekTo(position.toLong())
    mediaController?.playWhenReady
    mediaController?.play()
  }

  private fun initialListener() {
    val sessionToken = SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
    controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    controllerFuture?.addListener({
      if (mediaController == null) {
        if (controllerFuture != null) {
          mediaController = controllerFuture!!.get().also {
            it.addListener(playerStateListener)
          }
        }
      }
    }, MoreExecutors.directExecutor())

    playerStateListener = object : Player.Listener {
      override fun onIsPlayingChanged(isPlaying: Boolean) {
        _mediaPlayerState.update { it.copy(isPlaying = isPlaying) }
      }

      override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        _mediaPlayerState.update { it.copy(mediaMetadata = mediaMetadata) }
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
          Player.STATE_IDLE or Player.STATE_ENDED -> _mediaPlayerState.update { it.copy(isPlaying = false) }
          Player.STATE_BUFFERING -> _mediaPlayerState.update { it.copy(isPlaying = true) }
          else -> {}
        }
      }
    }

  }

}
