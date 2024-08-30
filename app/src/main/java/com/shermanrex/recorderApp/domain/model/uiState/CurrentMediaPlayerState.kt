package com.shermanrex.recorderApp.domain.model.uiState

import androidx.media3.common.MediaMetadata

// handle player state which come from listener flow
data class CurrentMediaPlayerState(
  var isPlaying: Boolean = false,
  var mediaMetadata: MediaMetadata = MediaMetadata.EMPTY,
) {
  companion object {
    val Empty = CurrentMediaPlayerState(
      isPlaying = false,
      mediaMetadata = MediaMetadata.EMPTY,
    )
  }
}