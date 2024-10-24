package com.shermanrex.recorderApp.data.util

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.common.truth.Truth.assertThat
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetMetaDataTest {

  @MockK
  lateinit var getMetaData: GetMetaData

  @Before
  fun setup() {
    getMetaData = mockk<GetMetaData>()
  }

  @Test
  fun getFileMetaData_return_recordModel_if_documentFile_is_not_null() = runTest {
    val documentFile = mockk<DocumentFile>()
    val recordModel = RecordModel(
      path = Uri.EMPTY,
      fullName = "test name",
      name = "test name",
      duration = 0,
      format = "m4a",
      bitrate = 128_000,
      sampleRate = 44_100,
      date = "1970/01/01",
      size = 0,
      id = UUID.randomUUID()
    )
    coEvery { getMetaData.get(any()) } returns recordModel

    val result = getMetaData.get(documentFile)

    assertThat(result).isEqualTo(recordModel)
  }

  @Test
  fun getFileMetaData_return_null_if_documentFile_is_null() = runTest {
    val documentFile = mockk<DocumentFile>()
    coEvery { getMetaData.get(any()) } returns null

    val result = getMetaData.get(documentFile)

    assertThat(result).isNull()
  }

}