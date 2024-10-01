package com.shermanrex.recorderApp.domain.useCase.storage

import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseCreateDocumentFile @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(fileName: String, savePath: String): DocumentFile? {
    return storageManager.createDocumentFile(fileName = fileName, savePath = savePath)
  }
}