package com.shermanrex.recorderApp.data.model

import android.media.MediaRecorder

sealed class AudioFormat(
  var name: String,
  var bitRate: List<Int>,
  var sampleRate: List<Int>,
  var defaultBitRate: Int,
  var defaultSampleRate: Int,
  var audioEncoder: Int,
  var outputFormat: Int,
) {
  data object M4A : AudioFormat(
    name = "m4a",
    bitRate = listOf(98_000, 128_000, 198_000, 256_000, 320_000),
    sampleRate = listOf(48_000, 44_100),
    defaultBitRate = 128_000,
    defaultSampleRate = 44_100,
    audioEncoder = MediaRecorder.AudioEncoder.AAC,
    outputFormat = MediaRecorder.OutputFormat.MPEG_4
  )

  data object THREEGPP : AudioFormat(
    name = "3gp",
    bitRate = listOf(98_000, 128_000, 198_000),
    sampleRate = listOf(48_000, 44_100),
    defaultBitRate = 98_000,
    defaultSampleRate = 44_100,
    audioEncoder = MediaRecorder.AudioEncoder.AAC,
    outputFormat = MediaRecorder.OutputFormat.THREE_GPP
  )

  data object WAV : AudioFormat(
    name = "wav",
    bitRate = listOf(1411_000),
    sampleRate = listOf(44_100),
    defaultBitRate = 1411_000,
    defaultSampleRate = 44_100,
    audioEncoder = MediaRecorder.AudioEncoder.AAC,
    outputFormat = MediaRecorder.OutputFormat.MPEG_4
  )
}