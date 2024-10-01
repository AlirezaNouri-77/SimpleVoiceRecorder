package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.dataStore.SavePath
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UseCaseGetSavePath @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  operator fun invoke(): Flow<SavePath> = dataStoreManager.getSavePath
}