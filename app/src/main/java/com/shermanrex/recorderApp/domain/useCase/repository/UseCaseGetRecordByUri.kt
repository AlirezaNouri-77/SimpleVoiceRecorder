package com.shermanrex.recorderApp.domain.useCase.repository

import android.net.Uri
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.domain.model.RecordModel
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class UseCaseGetRecordByUri @Inject constructor(
  private var recordRepository: RecordRepository,
) {
  suspend operator fun invoke(uri: Uri): Deferred<RecordModel?> {
    return recordRepository.getRecordByUri(uri)
  }
}