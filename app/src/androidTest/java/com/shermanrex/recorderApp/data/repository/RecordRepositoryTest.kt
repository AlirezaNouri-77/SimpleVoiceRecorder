package com.shermanrex.recorderApp.data.repository

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.common.truth.Truth.assertThat
import com.shermanrex.recorderApp.domain.model.repository.Failure
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.domain.model.repository.RepositoryResult
import com.shermanrex.recorderApp.domain.model.repository.RootFailure
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RecordRepositoryTest {

  @MockK
  private lateinit var recordRepository: RecordRepository

  @Before
  fun setup() {
    recordRepository = mockk<RecordRepository>()
  }

  @Test
  fun getRecords_return_loading() = runTest {
    val documentFile = mockk<DocumentFile>()
    coEvery { recordRepository.getRecords(any()) } returns flow {
      emit(RepositoryResult.Loading)
    }

    val test = recordRepository.getRecords(documentFile).first()

    assertThat(test).isEqualTo(RepositoryResult.Loading)
  }

  @Test
  fun getRecords_return_success_list() = runTest {
    val resultList = mutableListOf<RepositoryResult<RecordModel, Failure>>()
    val documentFile = mockk<DocumentFile>()
    coEvery { recordRepository.getRecords(any()) } returns flow {
      emit(RepositoryResult.Loading)
      emit(RepositoryResult.Success(listOf()))
    }

    recordRepository.getRecords(documentFile).collect { resultList.add(it) }

    assertThat(resultList).containsExactly(
      RepositoryResult.Loading,
      RepositoryResult.Success<RecordModel>(listOf())
    )
  }

  @Test
  fun getRecords_return_fail_empty() = runTest {
    val resultList = mutableListOf<RepositoryResult<RecordModel, Failure>>()
    val documentFile = mockk<DocumentFile>()
    coEvery { recordRepository.getRecords(any()) } returns flow {
      emit(RepositoryResult.Loading)
      emit(RepositoryResult.Failure(Failure.Empty))
    }

    recordRepository.getRecords(documentFile).collect { resultList.add(it) }

    assertThat(resultList).containsExactly(
      RepositoryResult.Loading,
      RepositoryResult.Failure<RootFailure>(Failure.Empty)
    )
  }

  @Test
  fun getRecord_by_single_uri_return_recordModel_if_uri_valid() = runTest {

    val record = mockk<RecordModel>()

    coEvery { recordRepository.getRecordByUri(any()).await() } returns record
    val test = recordRepository.getRecordByUri(Uri.EMPTY).await()

    assertThat(test).isEqualTo(record)
  }

  @Test
  fun getRecord_by_single_uri_return_null_if_uri_not_valid() = runTest {

    coEvery { recordRepository.getRecordByUri(any()).await() } returns null
    val test = recordRepository.getRecordByUri(Uri.EMPTY).await()

    assertThat(test).isNull()
  }

  @Test
  fun getRecord_by_single_uri_return_deferred() = runTest {

    val deferred = CompletableDeferred<RecordModel>()

    coEvery { recordRepository.getRecordByUri(any()) } returns deferred
    val test = recordRepository.getRecordByUri(Uri.EMPTY)

    assertThat(test).isEqualTo(deferred)
  }

}