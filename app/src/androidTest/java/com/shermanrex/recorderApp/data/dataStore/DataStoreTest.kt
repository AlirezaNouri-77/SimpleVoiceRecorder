package com.shermanrex.recorderApp.data.dataStore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager.Companion.BIT_RATE_KEY
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager.Companion.SAMPLE_RATE_KEY
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.SettingNameFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DataStoreTest {

  @get:Rule
  val templeFile: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

  private val dispatcher = UnconfinedTestDispatcher()
  private val coroutineScope = TestScope(dispatcher)

  private lateinit var dataStoreManager: DataStoreManager

  private var datastore = PreferenceDataStoreFactory.create(
    scope = coroutineScope,
    produceFile = {
      templeFile.newFile("test.preferences_pb")
    }
  )

  @Before
  fun setup() {
    dataStoreManager = DataStoreManager(datastore = datastore, dispatcherIO = dispatcher)
  }

  @Test
  fun savePathIsEmpty() {
    runBlocking {
      val path = dataStoreManager.getSavePath.first()
      assertThat(path).isEmpty()
    }
  }

  @Test
  fun writeSavePath() {
    runBlocking {
      dataStoreManager.writeSavePath("testPath")
      val path = dataStoreManager.getSavePath.first()
      assertThat(path).isNotEmpty()
    }
  }

  @Test
  fun isFirstTimeAppLaunch() {
    runBlocking {
      val boolean = dataStoreManager.getIsFirstTimeAppLaunch.first()
      assertThat(boolean).isTrue()
    }
  }

  @Test
  fun isNotFirstTimeAppLaunch() {
    runBlocking {
      dataStoreManager.writeFirstTimeAppLaunch(false)
      val boolean = dataStoreManager.getIsFirstTimeAppLaunch.first()
      assertThat(boolean).isFalse()
    }
  }

  @Test
  fun getNameFormat() {
    val expectResult = SettingNameFormat.entries.toTypedArray().toList()
    runBlocking {
      val boolean = dataStoreManager.getNameFormat.first()
      assertThat(boolean).isIn(expectResult.toList())
    }
  }

  @Test
  fun writeNameFormat(){
    val write = 1
    val expectResult = 1
    runBlocking {
      dataStoreManager.writeNameFormat(write)
      val result = dataStoreManager.getNameFormat.first().id
      assertThat(result).isEqualTo(expectResult)
    }
  }

  @Test
  fun getDefaultAudioFormat() {
    val expectResult = AudioFormat.M4A
    runBlocking {
      val result = dataStoreManager.getAudioFormat.first().format
      assertThat(result).isEqualTo(expectResult)
    }
  }

  @Test
  fun isCorrectWriteAudioFormat() {
    val writeFormat = AudioFormat.M4A
    val expectResult = AudioFormat.M4A
    runBlocking {
      dataStoreManager.writeAudioFormat(writeFormat)
      val result = dataStoreManager.getAudioFormat.first().format
      assertThat(result).isEqualTo(expectResult)
    }
  }

  @Test
  fun isNotCorrectWriteAudioFormat() {
    val writeFormat = AudioFormat.THREEGPP
    val expectResult = AudioFormat.M4A
    runBlocking {
      dataStoreManager.writeAudioFormat(writeFormat)
      val result = dataStoreManager.getAudioFormat.first().format
      assertThat(result).isNotEqualTo(expectResult)
    }
  }

  @Test
  fun writeAudioBitrate() {
    val writeFormat = 320_000
    val expectResult = 320_000
    runBlocking {
      dataStoreManager.writeAudioBitrate(writeFormat)
      val result = datastore.data.map { it[BIT_RATE_KEY] }.first() ?: 0
      assertThat(result).isEqualTo(expectResult)
    }
  }

  @Test
  fun writeSampleRate() {
    val writeFormat = 44_100
    val expectResult = 44_100
    runBlocking {
      dataStoreManager.writeSampleRate(writeFormat)
      val result = datastore.data.map { it[SAMPLE_RATE_KEY] }.first() ?: 0
      assertThat(result).isEqualTo(expectResult)
    }
  }

}