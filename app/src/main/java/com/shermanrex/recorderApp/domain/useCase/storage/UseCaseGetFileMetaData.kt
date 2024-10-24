package com.shermanrex.recorderApp.domain.useCase.storage

import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.data.util.GetMetaData
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import javax.inject.Inject

class UseCaseGetFileMetaData @Inject constructor(
  private var getMetaData: GetMetaData,
) {
  suspend operator fun invoke(documentFile: DocumentFile): RecordModel? {
    return getMetaData.get(documentFile)
  }
}