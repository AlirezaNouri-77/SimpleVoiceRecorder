package com.shermanrex.recorderApp.data.model

data class RecordAudioSetting(
  var format: AudioFormat,
  var bitrate: Int,
  var sampleRate: Int,
)