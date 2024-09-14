package com.shermanrex.recorderApp.presentation.screen.recorder

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
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.data.service.connection.MediaPlayerServiceConnection
import com.shermanrex.recorderApp.data.service.connection.MediaRecorderServiceConnection
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.data.util.convertTimeStampToDate
import com.shermanrex.recorderApp.data.util.removeFileformat
import com.shermanrex.recorderApp.domain.model.AudioFormat
import com.shermanrex.recorderApp.domain.model.DropDownMenuStateUi
import com.shermanrex.recorderApp.domain.model.Failure
import com.shermanrex.recorderApp.domain.model.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.domain.model.RecorderState
import com.shermanrex.recorderApp.domain.model.RepositoryResult
import com.shermanrex.recorderApp.domain.model.SettingNameFormat
import com.shermanrex.recorderApp.domain.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiEvent
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRecorderViewModel @Inject constructor(
  var serviceConnection: MediaRecorderServiceConnection,
  private var dataStoreManager: DataStoreManager,
  private var recordRepository: RecordRepository,
  private val mediaPlayerServiceConnection: MediaPlayerServiceConnection,
  private val storageManager: StorageManager,
) : ViewModel() {

  var screenRecorderScreenUiState = mutableStateOf(RecorderScreenUiState.LOADING)

  var recordDataList = mutableStateListOf<RecordModel>()
  var amplitudesList = mutableStateListOf<Float>()

  var currentAudioFormat by mutableStateOf(RecordAudioSetting.Empty)

  private var _uiEvent = MutableSharedFlow<RecorderScreenUiEvent>()
  var uiEvent = _uiEvent.asSharedFlow()

  private var _recordTime = MutableStateFlow(0)
  var recordTime = _recordTime.asStateFlow()

  var recorderState by mutableStateOf(RecorderState.IDLE)

  var mediaPlayerState by mutableStateOf(CurrentMediaPlayerState.Empty)
  var currentPlayerPosition by mutableFloatStateOf(0f)

  var dropDownMenuState by mutableStateOf(DropDownMenuStateUi.Empty)
  var showSettingBottomSheet by mutableStateOf(false)

  // var showNamePickerDialog by mutableStateOf(false)
  var currentIndexClick by mutableIntStateOf(-1)

  init {
    getRecords()
    initialAudioSetting()
    observeMediaPlayerStateListener()
    observeMediaRecorderService()
    observeMediaPlayerCurrentPosition()
  }

  suspend fun getDataStoreNameFormat(): SettingNameFormat = dataStoreManager.getNameFormat.first()

  suspend fun getDataStoreSavePath(): String = dataStoreManager.getSavePath.first()

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
    viewModelScope.launch {
      stopPlayAudio()
      recordDataList.remove(recordModel)
      storageManager.deleteRecord(recordModel.path)
      if (recordDataList.size == 0) screenRecorderScreenUiState.value = RecorderScreenUiState.EMPTY
    }
  }

  fun renameRecord(targetItem: RecordModel, newName: String) {
    viewModelScope.launch {
      val renameUri = storageManager.renameRecord(targetItem.path, newName)
      val result = storageManager.appendFileExtension(renameUri, targetItem.format)
      if (result == Uri.EMPTY) return@launch

      val nameAfterRename = storageManager.getRenameRecordName(result)
      val targetItemIndex = recordDataList.indexOf(targetItem)
      val updateRecord = recordDataList[targetItemIndex].copy(
        path = result,
        name = nameAfterRename.removeFileformat()
      )
      recordDataList.set(index = targetItemIndex, element = updateRecord)
    }
  }


  fun startRecord(customFileName: String) {
    viewModelScope.launch {

      val savePath = dataStoreManager.getSavePath.first()
      val nameFormat = dataStoreManager.getNameFormat.first()
      val audioRecordSetting = dataStoreManager.getAudioFormat.first()

      val fileName = customFileName.ifEmpty { convertTimeStampToDate(nameFormat.pattern) }
      val document = storageManager.createDocumentFile(fileName = fileName, savePath = savePath)

      if (document != null) {
        val fileDescriptor = storageManager.getSavePath(document)
        if (fileDescriptor != null) {
          sendActionToService(ServiceActionNotification.START)
          serviceConnection.mService.startRecord(
            fileDescriptor = fileDescriptor,
            recordAudioSetting = audioRecordSetting,
            fileSaveUri = document.uri,
          )
        }
      } else {
        setUiEvent(RecorderScreenUiEvent.SAF_PATH)
      }
    }
  }


  fun stopRecord() = serviceConnection.mService.stopRecord()

  fun writeDataStoreSavePath(savePath: String, shouldUpdateList: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeSavePath(savePath)
    if (shouldUpdateList) getRecords()
  }

  fun sendActionToService(action: ServiceActionNotification) =
    serviceConnection.sendIntentToService(action)

  fun pauseRecord() {
    if (recorderState == RecorderState.RECORDING) {
      serviceConnection.mService.pauseRecord()
    }
  }

  fun resumeRecord() = serviceConnection.mService.resumeRecord()

  fun startPlayAudio(recordModel: RecordModel) =
    mediaPlayerServiceConnection.startPlayAudio(recordModel)

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

  private fun getRecords() = viewModelScope.launch {
    val savePath = dataStoreManager.getSavePath.first()
    val documentFile = storageManager.getSavePathDocumentFile(savePath.toUri())

    if (documentFile == null) {
      setUiEvent(RecorderScreenUiEvent.SAF_PATH)
      return@launch
    }

    recordRepository.getRecords(documentFile)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), RepositoryResult.Loading)
      .collectLatest { result ->
        viewModelScope.launch {
          when (result) {
            is RepositoryResult.Success -> {
              recordDataList.clear()
              recordDataList.addAll(result.data)
              screenRecorderScreenUiState.value = RecorderScreenUiState.DATA
            }

            is RepositoryResult.Failure -> when (result.error) {
              Failure.Empty -> screenRecorderScreenUiState.value = RecorderScreenUiState.EMPTY
            }

            RepositoryResult.Loading -> screenRecorderScreenUiState.value = RecorderScreenUiState.LOADING
          }
        }
      }
  }

  private fun observeMediaPlayerStateListener() = viewModelScope.launch {
    mediaPlayerServiceConnection.mediaPlayerState.collectLatest {
      mediaPlayerState = it
    }
  }

  fun setUiEvent(event: RecorderScreenUiEvent) = viewModelScope.launch {
    _uiEvent.emit(event)
  }

  private fun initialAudioSetting() = viewModelScope.launch {
    currentAudioFormat = dataStoreManager.getAudioFormat.first()
  }

  private fun observeMediaRecorderService() {
    viewModelScope.launch(Dispatchers.IO) {
      val isServiceBind = serviceConnection.bindService()
      if (isServiceBind) {
        launch {
          serviceConnection.mService.amplitudes.collect {
            viewModelScope.launch {
              amplitudesList.add(it)
            }
          }
        }
        launch {
          serviceConnection.mService.recordTimer.collectLatest { time ->
            viewModelScope.launch {
              _recordTime.value = time
            }
          }
        }
        launch {
          serviceConnection.mService.recorderState.collectLatest { recordState ->
            viewModelScope.launch {
              recorderState = recordState
            }
          }
        }
        launch {
          serviceConnection.mService.playerEndChannel.receiveAsFlow().collect { uri ->
            viewModelScope.launch {
              recordRepository.getLastRecord(uri).await()?.let { recordDataList.add(0, it) }
              if (recordDataList.size != 0) screenRecorderScreenUiState.value = RecorderScreenUiState.DATA
              amplitudesList.clear()
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
          currentPlayerPosition = currentPosition?.toFloat() ?: 0f
        }
    }
  }

}
