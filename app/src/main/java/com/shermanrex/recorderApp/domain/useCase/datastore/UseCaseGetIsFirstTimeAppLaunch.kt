package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UseCaseGetIsFirstTimeAppLaunch @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  operator fun invoke(): Flow<Boolean> = dataStoreManager.getIsFirstTimeAppLaunch
}