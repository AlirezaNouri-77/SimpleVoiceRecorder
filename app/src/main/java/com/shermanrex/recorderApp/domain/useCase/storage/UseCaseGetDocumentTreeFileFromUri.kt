package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseGetDocumentTreeFileFromUri @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri): DocumentFile? {
    return storageManager.getDocumentTreeFileFromUri(uri)
  }
}