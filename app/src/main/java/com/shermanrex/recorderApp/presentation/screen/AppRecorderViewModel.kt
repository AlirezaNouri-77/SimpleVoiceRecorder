package com.shermanrex.recorderApp.presentation.screen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.model.AudioFormat
import com.shermanrex.recorderApp.data.model.RecordAudioSetting
import com.shermanrex.recorderApp.data.model.RecordModel
import com.shermanrex.recorderApp.data.model.RecorderState
import com.shermanrex.recorderApp.data.model.RepositoryResult
import com.shermanrex.recorderApp.data.model.SettingNameFormat
import com.shermanrex.recorderApp.data.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.data.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.data.model.uiState.RecorderScreenUiState
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.data.repository.StorageManager
import com.shermanrex.recorderApp.data.service.connection.MediaPlayerServiceConnection
import com.shermanrex.recorderApp.data.service.connection.MediaRecorderServiceConnection
import com.shermanrex.recorderApp.data.util.convertTimeStampToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppRecorderViewModel @Inject constructor(
  var serviceConnection: MediaRecorderServiceConnection,
  private var dataStoreManager: DataStoreManager,
  private var recordRepository: RecordRepository,
  private val mediaPlayerServiceConnection: MediaPlayerServiceConnection,
  private val storageManager: StorageManager,
) : ViewModel() {


  var screenRecorderScreenUiState = mutableStateOf(RecorderScreenUiState.INITIAL)

  var recordDataList = mutableStateListOf<RecordModel>()
  var amplitudesList = mutableStateListOf<Float>()

  var currentAudioFormat by mutableStateOf(
    RecordAudioSetting(
      format = AudioFormat.M4A,
      bitrate = 128000,
      sampleRate = 44100,
    )
  )
  var recordTime by mutableIntStateOf(0)
  var recorderState by mutableStateOf(RecorderState.IDLE)

  var mediaPlayerState by mutableStateOf(CurrentMediaPlayerState())
  var currentPlayerPosition by mutableFloatStateOf(0f)


  init {
    getRecords()
    initialAudioSetting()
    observeMediaPlayerStateListener()
    observeMediaRecorderService()
    observeMediaPlayerCurrentPosition()
  }

  suspend fun getDataStoreNameFormat(): SettingNameFormat {
    return dataStoreManager.getNameFormat.stateIn(viewModelScope).value
  }

  suspend fun getDataStoreSavePath(): String {
    return dataStoreManager.getSavePath.stateIn(viewModelScope).value
  }

  fun writeAudioBitrate(bitrate: Int) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeAudioBitrate(bitrate)
  }

  fun writeAudioFormat(audioFormat: AudioFormat) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeAudioFormat(audioFormat)
    dataStoreManager.writeSampleRate(audioFormat.defaultSampleRate)
    dataStoreManager.writeAudioBitrate(audioFormat.defaultBitRate)
  }

  fun writeSampleRate(sampleRate: Int) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeSampleRate(sampleRate)
  }


  fun writeDataStoreNameFormat(nameFormat: SettingNameFormat) {
    viewModelScope.launch {
      dataStoreManager.writeNameFormat(nameFormat.id)
    }
  }

  fun deleteRecord(recordModel: RecordModel) {
    viewModelScope.launch(Dispatchers.Main) {
      stopPlayAudio()
      recordDataList.remove(recordModel)
      storageManager.deleteRecord(recordModel.path)
    }
  }

  fun renameRecord(targetItem: RecordModel, newName: String) {
    viewModelScope.launch {
      val name = newName + ".${targetItem.format}"
      val result = storageManager.renameRecord(targetItem.path, name)
      if (result != Uri.EMPTY) {
        recordDataList.find { it == targetItem }?.path = result
        recordDataList.find { it == targetItem }?.name = newName
      }
    }
  }


  fun startRecord(customFileName: String) {
    viewModelScope.launch {
      val savePath = dataStoreManager.getSavePath.stateIn(viewModelScope).value
      val nameFormat = dataStoreManager.getNameFormat.stateIn(viewModelScope).value
      val audioRecordSetting = dataStoreManager.getAudioFormat.stateIn(viewModelScope).value
      val fileName =
        customFileName.ifEmpty { convertTimeStampToDate(nameFormat.pattern) } + ".${audioRecordSetting.format.name}"
      val fileDescriptor =
        storageManager.getSavePath(savePath = savePath, fileName = fileName)
      if (fileDescriptor != null) {
        serviceConnection.mService.startRecord(fileDescriptor, audioRecordSetting)
      } else {
        // Todo about null safety
      }
    }
  }


  fun stopRecord() {
    serviceConnection.mService.stopRecord()
    amplitudesList.clear()
    viewModelScope.launch {
      recordRepository.getLastRecord().await()?.let { recordDataList.add(it) }
    }
  }

  fun saveDataStore(savePath: String) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeSavePath(savePath)
  }

  fun sendActionToService(action: ServiceActionNotification) =
    serviceConnection.sendIntentToService(action)

  fun pauseRecord() {
    if (recorderState == RecorderState.RECORDING) {
      serviceConnection.mService.pauseRecord()
    }
  }

  fun resumeRecord() = serviceConnection.mService.resumeRecord()

  fun startPlayAudio(recordModel: RecordModel) = mediaPlayerServiceConnection.startPlayAudio(recordModel)
  fun stopPlayAudio() {
    mediaPlayerServiceConnection.stopPlayAudio()
    amplitudesList.clear()
  }

  fun resumeAudio() = mediaPlayerServiceConnection.resumeAudio()
  fun pauseAudio() = mediaPlayerServiceConnection.pauseAudio()
  fun fastForwardAudio() = mediaPlayerServiceConnection.fastForwardAudio()
  fun fastBackForwardAudio() = mediaPlayerServiceConnection.backForwardAudio()
  fun seekToPosition(position: Float) {
    mediaPlayerServiceConnection.seekToPosition(position = position)
    currentPlayerPosition = position
  }

  fun getRecords() = viewModelScope.launch {
    val savePath = dataStoreManager.getSavePath.stateIn(viewModelScope).value
    recordRepository.getRecords(savePath.toUri())
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), RepositoryResult.Loading)
      .collectLatest { result ->
        withContext(Dispatchers.Main) {
          when (result) {
            is RepositoryResult.ListData -> {
              recordDataList.clear()
              recordDataList.addAll(result.data)
              screenRecorderScreenUiState.value = RecorderScreenUiState.DATA
            }
            RepositoryResult.Empty -> screenRecorderScreenUiState.value = RecorderScreenUiState.EMPTY
            RepositoryResult.Loading -> screenRecorderScreenUiState.value = RecorderScreenUiState.LOADING
          }
        }
      }
  }

  private fun observeMediaPlayerStateListener() = viewModelScope.launch {
    mediaPlayerServiceConnection.mediaPlayerState.stateIn(scope = viewModelScope).collectLatest {
      withContext(Dispatchers.Main) {
        mediaPlayerState = it
      }
    }
  }

  private fun initialAudioSetting() = viewModelScope.launch {
    currentAudioFormat = dataStoreManager.getAudioFormat.stateIn(this).value
  }

  private fun observeMediaRecorderService() {
    viewModelScope.launch {
      val isServiceBind = serviceConnection.bindService()
      if (isServiceBind) {
        launch(Dispatchers.IO) {
          serviceConnection.mService.amplitudes.stateIn(
            this, SharingStarted.Eagerly, 0f
          ).collectLatest {
            withContext(Dispatchers.Main) {
              if (amplitudesList.size > 800) {
                amplitudesList.removeLast()
              }
              amplitudesList.add(it)
            }
          }
        }
        launch(Dispatchers.IO) {
          serviceConnection.mService.recordTimer.stateIn(
            this, SharingStarted.Eagerly, 0
          ).collectLatest {
            withContext(Dispatchers.Main) {
              recordTime = it
            }
          }
        }
        launch(Dispatchers.IO) {
          serviceConnection.mService.recorderState.stateIn(
            this, SharingStarted.Eagerly, RecorderState.IDLE
          ).collectLatest {
            withContext(Dispatchers.Main) {
              recorderState = it
            }
          }
        }
      }
    }
  }

  private fun observeMediaPlayerCurrentPosition() {
    viewModelScope.launch {
      mediaPlayerServiceConnection.currentPosition.stateIn(this, SharingStarted.Eagerly, 0)
        .collectLatest { currentPosition ->
          withContext(Dispatchers.Main) {
            currentPlayerPosition = currentPosition?.toFloat() ?: 0f
          }
        }
    }
  }


}
