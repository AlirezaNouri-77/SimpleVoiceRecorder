package com.shermanrex.recorderApp.domain.useCase.datastore

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import javax.inject.Inject

class UseCaseWriteNameFormat @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) {
  suspend operator fun invoke(id: Int) = dataStoreManager.writeNameFormat(id)
}
