package com.shermanrex.recorderApp.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shermanrex.recorderApp.data.di.annotation.DispatcherIO
import com.shermanrex.recorderApp.domain.api.DataStoreManagerImpl
import com.shermanrex.recorderApp.domain.model.AudioFormat
import com.shermanrex.recorderApp.domain.model.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.SettingNameFormat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

typealias SavePath = String

class DataStoreManager @Inject constructor(
  private var datastore: DataStore<Preferences>,
  @DispatcherIO private var dispatcherIO: CoroutineDispatcher,
) : DataStoreManagerImpl {

  override var getSavePath: Flow<SavePath> = datastore.data.map {
    it[SAVE_PATH_KEY] ?: ""
  }.flowOn(dispatcherIO)

  override var getIsFirstTimeAppLaunch: Flow<Boolean> = datastore.data.map {
    it[IS_FIRST_TIME_APP_LAUNCH] ?: true
  }.flowOn(dispatcherIO)

  override var getNameFormat: Flow<SettingNameFormat> = datastore.data.map {
    when (it[NAME_FORMAT_KEY]) {
      1 -> SettingNameFormat.FULL_DATE_TIME
      2 -> SettingNameFormat.ASK_ON_RECORD
      3 -> SettingNameFormat.SEMI_DATE_TIME
      4 -> SettingNameFormat.TIME
      null -> SettingNameFormat.FULL_DATE_TIME
      else -> SettingNameFormat.FULL_DATE_TIME
    }
  }.flowOn(dispatcherIO)

  override var getAudioFormat: Flow<RecordAudioSetting> = datastore.data.map {
    val format = when (it[AUDIO_FORMAT_KEY]) {
      "m4a" -> AudioFormat.M4A
      "3gp" -> AudioFormat.THREEGPP
      "wav" -> AudioFormat.WAV
      else -> AudioFormat.M4A
    }
    RecordAudioSetting(
      format = format,
      bitrate = it[BIT_RATE_KEY] ?: if (format == AudioFormat.WAV) 1411 else 128000,
      sampleRate = it[SAMPLE_RATE_KEY] ?: 44100,
    )
  }.flowOn(dispatcherIO)

  override suspend fun writeFirstTimeAppLaunch(boolean: Boolean) {
    withContext(dispatcherIO) {
      datastore.edit {
        it[IS_FIRST_TIME_APP_LAUNCH] = boolean
      }
    }
  }

  override suspend fun writeAudioFormat(format: AudioFormat) {
    withContext(dispatcherIO) {
      datastore.edit {
        it[AUDIO_FORMAT_KEY] = when (format) {
          AudioFormat.M4A -> "m4a"
          AudioFormat.THREEGPP -> "3gp"
          AudioFormat.WAV -> "wav"
        }
      }
    }
  }

  override suspend fun writeNameFormat(id: Int) {
    withContext(dispatcherIO) {
      datastore.edit { it[NAME_FORMAT_KEY] = id }
    }
  }

  override suspend fun writeAudioBitrate(bitrate: Int) {
    withContext(dispatcherIO) {
      datastore.edit { it[BIT_RATE_KEY] = bitrate }
    }
  }

  override suspend fun writeSampleRate(sampleRate: Int) {
    withContext(dispatcherIO) {
      datastore.edit { it[SAMPLE_RATE_KEY] = sampleRate }
    }
  }

  override suspend fun writeSavePath(path: String) {
    withContext(dispatcherIO) {
      datastore.edit { it[SAVE_PATH_KEY] = path }
    }
  }

  companion object {
    val SAVE_PATH_KEY = stringPreferencesKey("SAVE_PATH_KEY")
    val NAME_FORMAT_KEY = intPreferencesKey("NAME_FORMAT_KEY")
    val BIT_RATE_KEY = intPreferencesKey("BIT_RATE_KEY")
    val SAMPLE_RATE_KEY = intPreferencesKey("SAMPLE_RATE_KEY")
    val AUDIO_FORMAT_KEY = stringPreferencesKey("AUDIO_FORMAT_KEY")
    val IS_FIRST_TIME_APP_LAUNCH = booleanPreferencesKey("IS_FIRST_TIME_APP_LAUNCH")
  }

}