package com.shermanrex.recorderApp.domain.model

data class RecordAudioSetting(
  var format: AudioFormat,
  var bitrate: Int,
  var sampleRate: Int,
){
  companion object {
    var Empty = RecordAudioSetting(
      format = AudioFormat.M4A,
      bitrate = 128000,
      sampleRate = 44100,
    )
  }
}