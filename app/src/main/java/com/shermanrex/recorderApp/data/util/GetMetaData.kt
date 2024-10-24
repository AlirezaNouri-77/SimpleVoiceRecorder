package com.shermanrex.recorderApp.data.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.di.annotation.DispatcherIO
import com.shermanrex.recorderApp.domain.api.GetMetaDataImpl
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMetaData @Inject constructor(
  private var context: Context,
  @DispatcherIO private var dispatcherIO: CoroutineDispatcher,
) : GetMetaDataImpl {
  override suspend fun get(documentFile: DocumentFile?): RecordModel? {
    return withContext(dispatcherIO) {
      runCatching {

        val mediaMeta = MediaMetadataRetriever().also { it.setDataSource(context, documentFile?.uri) }

        mediaMeta.use {

          val duration = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
          val date = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: ""
          val bitrate = mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt() ?: 0
          val sampleRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
              ?.toInt() ?: 0
          } else 0

          return@withContext if (duration > 500 && documentFile != null) {
            RecordModel(
              path = documentFile.uri,
              fullName = documentFile.name!!,
              name = documentFile.name!!.removeFileFormat(),
              format = documentFile.name!!.getFileFormat(),
              duration = duration,
              bitrate = bitrate,
              date = date,
              size = documentFile.length(),
              sampleRate = sampleRate,
            )
          } else null

        }
      }.getOrNull()
    }
  }
}