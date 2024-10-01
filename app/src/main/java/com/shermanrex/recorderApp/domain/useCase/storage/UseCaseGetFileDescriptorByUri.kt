package com.shermanrex.recorderApp.domain.useCase.storage

import android.net.Uri
import android.os.ParcelFileDescriptor
import com.shermanrex.recorderApp.data.storage.StorageManager
import javax.inject.Inject

class UseCaseGetFileDescriptorByUri @Inject constructor(
  private var storageManager: StorageManager,
) {
  suspend operator fun invoke(uri: Uri): ParcelFileDescriptor? {
    return storageManager.getFileDescriptorByUri(uri)
  }
}