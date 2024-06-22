package com.shermanrex.recorderApp.domain

import android.net.Uri
import com.shermanrex.recorderApp.data.model.RecordModel
import com.shermanrex.recorderApp.data.model.RepositoryResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface RecordRepositoryImpl {
  suspend fun getRecords(directoryPath: Uri): Flow<RepositoryResult<RecordModel>>
  suspend fun getLastRecord(targetUri: Uri): Deferred<RecordModel?>
}