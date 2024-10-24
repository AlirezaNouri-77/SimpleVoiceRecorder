package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.domain.model.record.SettingNameFormat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UseCaseGetNameFormat @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  operator fun invoke(): Flow<SettingNameFormat> = dataStoreManager.getNameFormat
}