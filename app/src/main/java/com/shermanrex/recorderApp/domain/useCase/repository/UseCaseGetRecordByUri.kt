package com.shermanrex.recorderApp.domain.useCase.repository

import android.net.Uri
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import javax.inject.Inject

class UseCaseGetRecordByUri @Inject constructor(
  private var recordRepository: RecordRepository,
) {
  suspend operator fun invoke(uri: Uri): RecordModel?  {
    return recordRepository.getRecordByUri(uri)
  }
}