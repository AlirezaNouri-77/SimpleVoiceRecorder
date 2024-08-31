package com.shermanrex.recorderApp.domain

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.domain.model.RecordModel

interface StorageManagerImpl {
  fun getSavePath(document: DocumentFile): ParcelFileDescriptor?
  suspend fun deleteRecord(uri: Uri)
  suspend fun createDocumentFile(fileName: String, savePath: String): DocumentFile?
  suspend fun renameRecord(uri: Uri, newName: String): Uri
  suspend fun appendFileExtension(uri: Uri, fileFormat: String): Uri
  suspend fun getFileDetailByMediaMetaRetriever(document: DocumentFile?): RecordModel?
  suspend fun getRenameRecordName(uri: Uri): String
}