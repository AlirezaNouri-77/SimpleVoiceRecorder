package com.shermanrex.recorderApp.domain.api

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.domain.model.RecordModel

interface StorageManagerImpl {
  suspend fun getFileDescriptorByUri(uri: Uri): ParcelFileDescriptor?
  suspend fun deleteRecord(uri: Uri): Boolean
  suspend fun createFileByDocumentFile(fileName: String, savePath: String): DocumentFile?
  suspend fun renameRecord(uri: Uri, newName: String): Uri
  suspend fun appendFileExtension(uri: Uri, fileFormat: String): Uri
  suspend fun getRenamedRecordName(uri: Uri): String
  suspend fun getDocumentTreeFileFromUri(uri: Uri): DocumentFile?
  suspend fun getDocumentFileFromUri(uri: Uri): DocumentFile?
}