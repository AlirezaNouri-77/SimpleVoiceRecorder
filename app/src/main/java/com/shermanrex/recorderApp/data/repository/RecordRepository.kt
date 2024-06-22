package com.shermanrex.recorderApp.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.model.RecordModel
import com.shermanrex.recorderApp.data.model.RepositoryResult
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.RecordRepositoryImpl
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecordRepository @Inject constructor(
  private var context: Context,
  private var storageManager: StorageManager,
) : RecordRepositoryImpl {

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun getRecords(directoryPath: Uri): Flow<RepositoryResult<RecordModel>> {
    return channelFlow {
      val documentFileUri = DocumentFile.fromTreeUri(context, directoryPath)
      val result = documentFileUri?.listFiles()?.map { document ->
        async {
          if (document.canRead() && document.length() > 0) {
            storageManager.getFileDetailByMediaMetaRetriever(
              document = document
            )
          } else null
        }
      }?.awaitAll()?.filterNotNull() ?: emptyList()
      if (result.isNotEmpty()) {
        send(RepositoryResult.ListData(result))
      } else {
        send(RepositoryResult.Empty)
      }

    }.flowOn(Dispatchers.IO.limitedParallelism(30))

  }

  override suspend fun getLastRecord(targetUri: Uri): Deferred<RecordModel?> {
    return withContext(Dispatchers.IO) {
      val document = DocumentFile.fromSingleUri(context, targetUri)
      async { storageManager.getFileDetailByMediaMetaRetriever(document) }
    }
  }

}



