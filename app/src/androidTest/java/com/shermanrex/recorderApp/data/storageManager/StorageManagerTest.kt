package com.shermanrex.recorderApp.data.storageManager

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.google.common.truth.Truth.assertThat
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.model.RecordModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class StorageManagerTest {

  @MockK
  lateinit var storageManager: StorageManager

  @Before
  fun setup() {
    storageManager = mockk()
  }

  @Test
  fun getFileDescriptorByUri_return_ParcelFileDescriptor_when_uri_is_valid() = runTest {
    val document = mockk<ParcelFileDescriptor>()
    coEvery { storageManager.getFileDescriptorByUri(any()) } returns document

    val result = storageManager.getFileDescriptorByUri(Uri.EMPTY)

    assertThat(result).isNotNull()
  }

  @Test
  fun getFileDescriptorByUri_return_null_ParcelFileDescriptor_when_uri_is_not_valid() = runTest {

    coEvery { storageManager.getFileDescriptorByUri(any()) } returns null

    val result = storageManager.getFileDescriptorByUri(Uri.EMPTY)

    assertThat(result).isNull()
  }

  @Test
  fun createFileByDocumentFile_return_DocumentFile_when_Save_Path_uri_is_not_valid() = runTest {
    val document = mockk<DocumentFile>()
    coEvery { storageManager.createFileByDocumentFile(any(), any()) } returns document

    val result = storageManager.createFileByDocumentFile("example", "example_path")

    assertThat(result).isNotNull()
  }

  @Test
  fun createFileByDocumentFile_return_null_DocumentFile_when_Save_Path_uri_is_not_valid() = runTest {

    coEvery { storageManager.createFileByDocumentFile(any(), any()) } returns null

    val result = storageManager.createFileByDocumentFile("example", "example_path")

    assertThat(result).isNull()
  }

  @Test
  fun deleteRecord_return_false_uri_is_not_valid() = runTest {

    coEvery { storageManager.deleteRecord(any()) } returns false

    val result = storageManager.deleteRecord(Uri.EMPTY)

    assertThat(result).isFalse()
  }

  @Test
  fun deleteRecord_return_true_uri_is_valid() = runTest {

    coEvery { storageManager.deleteRecord(any()) } returns true

    val result = storageManager.deleteRecord(Uri.EMPTY)

    assertThat(result).isTrue()
  }

  @Test
  fun documentTreeFileFromUri_return_DocumentFileTreeUri_uri_is_valid() = runTest {
    val document = mockk<DocumentFile>()
    coEvery { storageManager.getDocumentTreeFileFromUri(any()) } returns document

    val result = storageManager.getDocumentTreeFileFromUri(Uri.EMPTY)

    assertThat(result).isNotNull()
  }

  @Test
  fun documentTreeFileFromUri_return_Null_DocumentFile_uri_is_not_valid() = runTest {
    coEvery { storageManager.getDocumentTreeFileFromUri(any()) } returns null

    val result = storageManager.getDocumentTreeFileFromUri(Uri.EMPTY)

    assertThat(result).isNull()
  }

  @Test
  fun documentFileFromUri_return_Null_DocumentFile_uri_is_not_valid() = runTest {
    coEvery { storageManager.getDocumentFileFromUri(any()) } returns null

    val result = storageManager.getDocumentFileFromUri(Uri.EMPTY)

    assertThat(result).isNull()
  }

  @Test
  fun documentFileFromUri_return_documentFile_uri_is_valid() = runTest {
    val document = mockk<DocumentFile>()
    coEvery { storageManager.getDocumentFileFromUri(any()) } returns document

    val result = storageManager.getDocumentFileFromUri(Uri.EMPTY)

    assertThat(result).isNotNull()
  }

  @Test
  fun renameFile_return_uri_if_uri_is_valid() = runTest {

    coEvery { storageManager.renameRecord(any(), any()) } returns Uri.parse("example_uri")
    val result = storageManager.renameRecord(Uri.EMPTY, "example_name")

    assertThat(result).isEqualTo(Uri.parse("example_uri"))
  }

  @Test
  fun renameFile_return_empty_uri_if_uri_is_not_valid() = runTest {
    coEvery { storageManager.renameRecord(any(), any()) } returns Uri.EMPTY

    val result = storageManager.renameRecord(Uri.EMPTY, "example_name")

    assertThat(result).isEqualTo(Uri.EMPTY)
  }

  @Test
  fun appendFileExtension_return_uri_if_uri_is_valid() = runTest {
    coEvery { storageManager.appendFileExtension(any(), any()) } returns Uri.parse("example_uri")

    val result = storageManager.appendFileExtension(Uri.EMPTY, "example_name")

    assertThat(result).isEqualTo(Uri.parse("example_uri"))
  }

  @Test
  fun appendFileExtension_return_empty_uri_if_uri_is_not_valid() = runTest {
    coEvery { storageManager.appendFileExtension(any(), any()) } returns Uri.EMPTY

    val result = storageManager.appendFileExtension(Uri.EMPTY, "example_name")

    assertThat(result).isEqualTo(Uri.EMPTY)
  }

  @Test
  fun getRenamedRecordName_return_correct_name_if_uri_is_valid() = runTest {
    coEvery { storageManager.getRenamedRecordName(any()) } returns "example_name"

    val result = storageManager.getRenamedRecordName(Uri.EMPTY)

    assertThat(result).isEqualTo("example_name")
  }

  @Test
  fun getRenamedRecordName_return_not_correct_name_if_uri_is_not_valid() = runTest {
    coEvery { storageManager.getRenamedRecordName(any()) } returns "example_name"

    val result = storageManager.getRenamedRecordName(Uri.EMPTY)

    assertThat(result).isNotEqualTo("")
  }

}
