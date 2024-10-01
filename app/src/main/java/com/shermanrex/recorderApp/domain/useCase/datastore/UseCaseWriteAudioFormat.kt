package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.domain.model.AudioFormat
import javax.inject.Inject

class UseCaseWriteAudioFormat @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  suspend operator fun invoke(format: AudioFormat) = dataStoreManager.writeAudioFormat(format)
}

