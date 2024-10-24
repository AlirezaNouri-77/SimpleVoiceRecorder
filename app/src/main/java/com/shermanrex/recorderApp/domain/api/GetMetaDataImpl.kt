package com.shermanrex.recorderApp.domain.api

import androidx.documentfile.provider.DocumentFile
import com.shermanrex.recorderApp.domain.model.record.RecordModel

interface GetMetaDataImpl {
  suspend fun get(documentFile: DocumentFile?): RecordModel?
}