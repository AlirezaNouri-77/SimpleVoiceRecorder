package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseDeleteRecord @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri): Boolean {
    return storageManager.deleteRecord(uri)
  }
}