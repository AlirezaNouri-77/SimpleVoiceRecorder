package com.shermanrex.recorderApp.domain.model.uiState

import androidx.media3.common.MediaMetadata

// handle player state which come from listener flow
data class CurrentMediaPlayerState(
  var isPlaying: Boolean,
  var isBuffering: Boolean,
  var mediaMetadata: MediaMetadata,
) {
  companion object {
    val Empty = CurrentMediaPlayerState(
      isPlaying = false,
      isBuffering = false,
      mediaMetadata = MediaMetadata.EMPTY,
    )
  }
}