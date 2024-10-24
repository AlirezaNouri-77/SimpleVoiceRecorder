package com.shermanrex.recorderApp.domain.useCase.repository

import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.domain.model.repository.Failure
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.domain.model.repository.RepositoryResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UseCaseGetRecords @Inject constructor(
  private var recordRepository: RecordRepository,
) {
  suspend operator fun invoke(documentFile: DocumentFile): Flow<RepositoryResult<RecordModel, Failure>> {
    return recordRepository.getRecords(documentFile)
  }
}
