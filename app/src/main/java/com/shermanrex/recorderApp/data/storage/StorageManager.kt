package com.shermanrex.recorderApp.data.storage

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.di.annotation.DispatcherIO
import com.shermanrex.recorderApp.domain.api.StorageManagerImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageManager @Inject constructor(
  var context: Context,
  @DispatcherIO private var dispatcherIO: CoroutineDispatcher,
) : StorageManagerImpl {

  override suspend fun getFileDescriptorByUri(uri: Uri): ParcelFileDescriptor? {
    return withContext(dispatcherIO) {
      runCatching { context.contentResolver.openFileDescriptor(uri, "w") }.getOrNull()
    }
  }

  override suspend fun createFileByDocumentFile(fileName: String, savePath: String): DocumentFile? {
    return withContext(dispatcherIO) {
      val document = DocumentFile.fromTreeUri(context, Uri.parse(savePath))
      document?.createFile("audio/*", fileName)
    }
  }

  override suspend fun getDocumentTreeFileFromUri(uri: Uri): DocumentFile? {
    return withContext(dispatcherIO) {
      runCatching { DocumentFile.fromTreeUri(context, uri) }.getOrNull()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun deleteRecord(uri: Uri): Boolean {
    return withContext(dispatcherIO.limitedParallelism(1)) {
      val document = DocumentFile.fromSingleUri(context, uri)
      document?.delete() ?: false
    }
  }

  override suspend fun getDocumentFileFromUri(uri: Uri): DocumentFile? {
    return withContext(dispatcherIO) {
      kotlin.runCatching { DocumentFile.fromSingleUri(context, uri) }.getOrNull()
    }
  }

  override suspend fun renameRecord(uri: Uri, newName: String): Uri = withContext(dispatcherIO) {
    DocumentsContract.renameDocument(context.contentResolver, uri, newName) ?: Uri.EMPTY
  }

  override suspend fun appendFileExtension(uri: Uri, fileFormat: String): Uri {
    return withContext(dispatcherIO) {
      val targetFileName = DocumentFile.fromSingleUri(context, uri)?.name ?: ""
      val newNameWithExtension = targetFileName + ".${fileFormat}"
      renameRecord(uri = uri, newName = newNameWithExtension)
    }
  }

  override suspend fun getRenamedRecordName(uri: Uri): String {
    return withContext(dispatcherIO) {
      val document = DocumentFile.fromSingleUri(context, uri)
      return@withContext document?.name.toString()
    }
  }

}