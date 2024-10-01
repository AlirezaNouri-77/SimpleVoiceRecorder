package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import javax.inject.Inject

class UseCaseWriteSavePath @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  suspend operator fun invoke(path: String) = dataStoreManager.writeSavePath(path)
}