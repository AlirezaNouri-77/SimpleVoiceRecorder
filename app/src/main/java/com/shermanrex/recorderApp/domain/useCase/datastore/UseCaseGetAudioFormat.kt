package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.domain.model.RecordAudioSetting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UseCaseGetAudioFormat @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  operator fun invoke(): Flow<RecordAudioSetting> = dataStoreManager.getAudioFormat
}