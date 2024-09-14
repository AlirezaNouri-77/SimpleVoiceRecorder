package com.shermanrex.recorderApp.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.RecordRepositoryImpl
import com.shermanrex.recorderApp.domain.model.Failure
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.domain.model.RepositoryResult
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
  override suspend fun getRecords(documentFile: DocumentFile): Flow<RepositoryResult<RecordModel, Failure>> {
    return channelFlow {

      send(RepositoryResult.Loading)

      val result = documentFile.listFiles().map { document ->
        async {
          if (document.canRead() && document.length() > 0) {
            storageManager.getFileDetailByMediaMetaRetriever(
              document = document
            )
          } else null
        }
      }.awaitAll().filterNotNull().sortedByDescending { it.date }

      if (result.isNotEmpty()) {
        send(RepositoryResult.Success(result))
      } else {
        send(RepositoryResult.Failure(Failure.Empty))
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



