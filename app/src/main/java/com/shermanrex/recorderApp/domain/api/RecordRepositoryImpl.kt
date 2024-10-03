package com.shermanrex.recorderApp.domain.api

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.domain.model.Failure
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.domain.model.RepositoryResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface RecordRepositoryImpl {
  suspend fun getRecords(documentFile: DocumentFile): Flow<RepositoryResult<RecordModel, Failure>>
  suspend fun getRecordByUri(targetUri: Uri): Deferred<RecordModel?>
}