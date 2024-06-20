package com.shermanrex.recorderApp.data.mapper

import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.shermanrex.recorderApp.data.Constant.METADATA_DURATION_KEY
import com.shermanrex.recorderApp.data.Constant.METADATA_URI_KEY
import com.shermanrex.recorderApp.data.model.RecordModel

fun RecordModel.toMediaItem(): MediaItem {
  return MediaItem.Builder()
    .setMediaMetadata(
      MediaMetadata.Builder()
        .setDisplayTitle(this.name)
        .setExtras(
        bundleOf(
          METADATA_DURATION_KEY to this.duration,
          METADATA_URI_KEY to this.path,
        )
      ).build()
    )
    .setUri(this.path)
    .setMediaId(this.path.toString())
    .build()
}