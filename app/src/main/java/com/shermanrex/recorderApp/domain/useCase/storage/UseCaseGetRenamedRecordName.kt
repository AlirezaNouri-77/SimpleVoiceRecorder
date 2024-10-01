package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseGetRenamedRecordName @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri): String {
    return storageManager.getRenamedRecordName(uri = uri)
  }
}
