package com.shermanrex.recorderApp.domain.useCase.storage

import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.model.RecordModel
import javax.inject.Inject

class UseCaseGetFileMetaData @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(documentFile: DocumentFile): RecordModel? {
    return storageManager.getFileMetaData(documentFile)
  }
}