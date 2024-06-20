package com.shermanrex.recorderApp.data.model

import android.net.Uri
import androidx.compose.runtime.Stable
import java.util.UUID

@Stable
data class RecordModel(
  var path: Uri,
  var fullName: String,
  var name: String,
  var duration: Int,
  var format: String,
  var bitrate: Int,
  var sampleRate: Int,
  var size: Long,
  var id: UUID = UUID.randomUUID(),
)
