package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseRenameRecord @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri, newName: String): Uri {
    return storageManager.renameRecord(uri = uri, newName = newName)
  }
}