package com.shermanrex.recorderApp.data.storage

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.util.getFileFormat
import com.shermanrex.recorderApp.data.util.removeFileformat
import com.shermanrex.recorderApp.domain.StorageManagerImpl
import com.shermanrex.recorderApp.domain.model.RecordModel
import kotlinx.coroutines.Dispatchers
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
    return withContext(Dispatchers.IO) {
      val document = DocumentFile.fromTreeUri(context, Uri.parse(savePath))
      document?.createFile("audio/*", fileName)
    }
  }

  override suspend fun getSavePathDocumentFile(uri: Uri): DocumentFile? {
    return withContext(Dispatchers.IO) {
      runCatching { DocumentFile.fromTreeUri(context, uri) }.getOrNull()
    }
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

  override suspend fun appendFileExtension(uri: Uri, fileFormat: String): Uri {
    return withContext(Dispatchers.IO) {
      val targetFileName = DocumentFile.fromSingleUri(context, uri)?.name ?: ""
      val newNameWithExtension = targetFileName + ".${fileFormat}"
      DocumentsContract.renameDocument(context.contentResolver, uri, newNameWithExtension) ?: Uri.EMPTY
    }
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