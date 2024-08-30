package com.shermanrex.recorderApp.data.storage

import android.content.Context
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.data.util.getFileFormat
import com.shermanrex.recorderApp.data.util.removeFileformat
import com.shermanrex.recorderApp.domain.StorageManagerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageManager @Inject constructor(
  var context: Context,
) : StorageManagerImpl {

  override fun getSavePath(document: DocumentFile): ParcelFileDescriptor? {
    val fileDescriptor = context.contentResolver.openFileDescriptor(document.uri, "w")
    return fileDescriptor
  }

  override suspend fun createDocumentFile(fileName: String, savePath: String): DocumentFile? {
    val document = DocumentFile.fromTreeUri(context, Uri.parse(savePath))
    return document?.createFile("audio/*", fileName)
  }

  override suspend fun deleteRecord(uri: Uri) {
    withContext(Dispatchers.IO) {
      val document = DocumentFile.fromSingleUri(context, uri)
      document?.delete() ?: false
    }
  }

  override suspend fun renameRecord(uri: Uri, newName: String): Uri = withContext(Dispatchers.IO) {
    DocumentsContract.renameDocument(context.contentResolver, uri, newName) ?: Uri.EMPTY
  }

  override suspend fun getRenameRecordName(uri: Uri): String {
    return withContext(Dispatchers.IO) {
      val document = DocumentFile.fromSingleUri(context, uri)
      return@withContext document?.name.toString()
    }
  }

  override suspend fun getFileDetailByMediaMetaRetriever(
    document: DocumentFile?,
  ): RecordModel? {

    val mediaMeta = MediaMetadataRetriever()

    return withContext(Dispatchers.IO) {
      mediaMeta.setDataSource(context, document?.uri)

      mediaMeta.use {

        val duration = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
        val date = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: ""
        val bitrate = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt() ?: 0
        val sampleRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
            ?.toInt() ?: 0
        } else 0

        return@withContext if (duration > 500 && document != null) {
          RecordModel(
            path = document.uri,
            fullName = document.name!!,
            name = document.name!!.removeFileformat(),
            format = document.name!!.getFileFormat(),
            duration = duration,
            bitrate = bitrate,
            date = date,
            size = document.length(),
            sampleRate = sampleRate,
          )
        } else null

      }
    }

  }

}