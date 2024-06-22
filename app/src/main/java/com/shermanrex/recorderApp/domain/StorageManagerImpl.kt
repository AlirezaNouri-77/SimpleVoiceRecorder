package com.shermanrex.recorderApp.domain

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.model.RecordModel

interface StorageManagerImpl {
  fun getSavePath(document: DocumentFile): ParcelFileDescriptor?
  suspend fun deleteRecord(uri: Uri)
  suspend fun renameRecord(uri: Uri, newName: String): Uri
  suspend fun getFileDetailByMediaMetaRetriever(document: DocumentFile?): RecordModel?
}