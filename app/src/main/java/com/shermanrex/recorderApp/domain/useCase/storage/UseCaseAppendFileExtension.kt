package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseAppendFileExtension @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri, fileFormat: String): Uri {
    return storageManager.appendFileExtension(uri = uri, fileFormat = fileFormat)
  }
}