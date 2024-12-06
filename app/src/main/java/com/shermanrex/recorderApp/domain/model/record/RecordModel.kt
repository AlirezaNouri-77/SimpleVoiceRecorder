package com.shermanrex.recorderApp.domain.model.record

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
  var date: String,
  var size: Long,
  var id: UUID = UUID.randomUUID(),
){
  companion object {
    var Empty = RecordModel(
      path = Uri.EMPTY,
      fullName = "",
      name = "",
      duration = 0,
      format = "",
      bitrate = 0,
      sampleRate = 0,
      date = "",
      size = 0,
      id = UUID.randomUUID(),
    )
    var Dummy = RecordModel(
      path = Uri.EMPTY,
      fullName = "Example Record.m4a",
      name = "Example Record",
      duration = 15_000,
      format = "m4a",
      bitrate = 128_000,
      sampleRate = 44_100,
      size = 1_000_000,
      date = "",
    )
  }
}
