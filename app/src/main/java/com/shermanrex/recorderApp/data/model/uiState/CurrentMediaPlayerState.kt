package com.shermanrex.recorderApp.data.model.uiState

import androidx.media3.common.MediaMetadata

// handle player state which come from listener flow
data class CurrentMediaPlayerState(
  var isPlaying: Boolean = false,
  var mediaMetadata: MediaMetadata = MediaMetadata.EMPTY,
)