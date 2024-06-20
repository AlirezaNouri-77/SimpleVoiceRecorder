package com.shermanrex.recorderApp.domain

import com.shermanrex.recorderApp.data.dataStore.SavePath
import com.shermanrex.recorderApp.data.model.AudioFormat
import com.shermanrex.recorderApp.data.model.RecordAudioSetting
import com.shermanrex.recorderApp.data.model.SettingNameFormat
import kotlinx.coroutines.flow.Flow

interface DataStoreManagerImpl {
  var getSavePath: Flow<SavePath>
  var getNameFormat: Flow<SettingNameFormat>
  var getAudioFormat: Flow<RecordAudioSetting>
  suspend fun writeAudioFormat(format: AudioFormat)
  suspend fun writeSavePath(path: String)
  suspend fun writeAudioBitrate(bitrate: Int)
  suspend fun writeSampleRate(sampleRate: Int)
  suspend fun writeNameFormat(id: Int)
}