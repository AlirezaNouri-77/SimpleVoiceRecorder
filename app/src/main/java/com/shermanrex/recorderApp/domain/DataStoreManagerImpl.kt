package com.shermanrex.recorderApp.domain

import com.shermanrex.recorderApp.data.dataStore.SavePath
import com.shermanrex.recorderApp.domain.model.AudioFormat
import com.shermanrex.recorderApp.domain.model.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.SettingNameFormat
import kotlinx.coroutines.flow.Flow

interface DataStoreManagerImpl {
  var getSavePath: Flow<SavePath>
  var getNameFormat: Flow<SettingNameFormat>
  var getAudioFormat: Flow<RecordAudioSetting>
  var getIsFirstTimeAppLaunch: Flow<Boolean>
  suspend fun writeAudioFormat(format: AudioFormat)
  suspend fun writeSavePath(path: String)
  suspend fun writeAudioBitrate(bitrate: Int)
  suspend fun writeSampleRate(sampleRate: Int)
  suspend fun writeNameFormat(id: Int)
  suspend fun writeFirstTimeAppLaunch(boolean: Boolean)
}