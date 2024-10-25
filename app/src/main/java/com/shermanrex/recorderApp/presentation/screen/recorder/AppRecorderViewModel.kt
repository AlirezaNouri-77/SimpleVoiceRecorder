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
import com.shermanrex.recorderApp.data.service.connection.MediaPlayerServiceConnection
import com.shermanrex.recorderApp.data.service.connection.MediaRecorderServiceConnection
import com.shermanrex.recorderApp.data.util.convertTimeStampToDate
import com.shermanrex.recorderApp.data.util.removeFileFormat
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.ui.DropDownMenuStateUi
import com.shermanrex.recorderApp.domain.model.repository.Failure
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.domain.model.repository.RepositoryResult
import com.shermanrex.recorderApp.domain.model.record.SettingNameFormat
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiEvent
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiState
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetAudioFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetNameFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetSavePath
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteAudioBitrate
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteAudioFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteNameFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSampleRate
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSavePath
import com.shermanrex.recorderApp.domain.useCase.repository.UseCaseGetRecordByUri
import com.shermanrex.recorderApp.domain.useCase.repository.UseCaseGetRecords
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseAppendFileExtension
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseCreateDocumentFile
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseDeleteRecord
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetDocumentTreeFileFromUri
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetFileDescriptorByUri
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetRenamedRecordName
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseRenameRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRecorderViewModel @Inject constructor(
  var serviceConnection: MediaRecorderServiceConnection,
  private val mediaPlayerServiceConnection: MediaPlayerServiceConnection,
  private var useCaseGetAudioFormat: UseCaseGetAudioFormat,
  private var useCaseGetNameFormat: UseCaseGetNameFormat,
  private var useCaseWriteSavePath: UseCaseWriteSavePath,
  private var useCaseGetSavePath: UseCaseGetSavePath,
  private var useCaseWriteAudioBitrate: UseCaseWriteAudioBitrate,
  private var useCaseWriteAudioFormat: UseCaseWriteAudioFormat,
  private var useCaseWriteNameFormat: UseCaseWriteNameFormat,
  private var useCaseWriteSampleRate: UseCaseWriteSampleRate,
  private var useCaseGetRecords: UseCaseGetRecords,
  private var useCaseGetRecordByUri: UseCaseGetRecordByUri,
  private var useCaseDeleteRecord: UseCaseDeleteRecord,
  private var useCaseRenameRecord: UseCaseRenameRecord,
  private var useCaseAppendFileExtension: UseCaseAppendFileExtension,
  private var useCaseGetRenamedRecordName: UseCaseGetRenamedRecordName,
  private var useCaseCreateDocumentFile: UseCaseCreateDocumentFile,
  private var useCaseGetFileDescriptorByUri: UseCaseGetFileDescriptorByUri,
  private var useCaseGetDocumentTreeFileFromUri: UseCaseGetDocumentTreeFileFromUri,
) : ViewModel() {

  var screenRecorderScreenUiState = mutableStateOf(RecorderScreenUiState.LOADING)

  var recordDataList = mutableStateListOf<RecordModel>()
  var amplitudesList = mutableStateListOf<Float>()
  var selectedItemList = mutableStateListOf<RecordModel>()

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

  var showSelectMode by mutableStateOf(false)
  var currentItemIndex by mutableIntStateOf(-1)

  init {
    getRecords()
    initialAudioSetting()
    observeMediaPlayerStateListener()
    observeMediaRecorderService()
    observeMediaPlayerCurrentPosition()
  }

  suspend fun getDataStoreRecordNameFormat(): SettingNameFormat = useCaseGetNameFormat().first()

  suspend fun getDataStoreSavePath(): String = useCaseGetSavePath().first()

  fun writeAudioBitrate(bitrate: Int) = viewModelScope.launch {
    useCaseWriteAudioBitrate(bitrate)
  }

  fun writeAudioFormat(audioFormat: AudioFormat) = viewModelScope.launch {
    useCaseWriteAudioFormat(audioFormat)
    useCaseWriteSampleRate(audioFormat.defaultSampleRate)
    useCaseWriteAudioBitrate(audioFormat.defaultBitRate)
  }

  fun writeSampleRate(sampleRate: Int) = viewModelScope.launch {
    useCaseWriteSampleRate(sampleRate)
  }

  fun writeDataStoreNameFormat(nameFormat: SettingNameFormat) {
    viewModelScope.launch {
      useCaseWriteNameFormat(nameFormat.id)
    }
  }

  fun deleteRecord(recordModel: RecordModel) {
    viewModelScope.launch {
      if (mediaPlayerState.isPlaying) stopPlayAudio()
      if (useCaseDeleteRecord(recordModel.path)) {
        recordDataList.remove(recordModel)
      }
      if (recordDataList.size == 0) screenRecorderScreenUiState.value = RecorderScreenUiState.EMPTY
    }
  }

  fun deleteRecord() {
    if (selectedItemList.size == 0) return
    viewModelScope.launch {
      selectedItemList.forEach {
        deleteRecord(it)
      }
      selectedItemList.clear()
    }
  }

  fun renameRecord(targetItem: RecordModel, newName: String) {
    viewModelScope.launch {
      val renameUri = useCaseRenameRecord(targetItem.path, newName)
      val result = useCaseAppendFileExtension(renameUri, targetItem.format)
      if (result == Uri.EMPTY) return@launch

      val nameAfterRename = useCaseGetRenamedRecordName(result)
      val targetItemIndex = recordDataList.indexOf(targetItem)
      val updateRecord = recordDataList[targetItemIndex].copy(
        path = result,
        name = nameAfterRename.removeFileFormat()
      )
      recordDataList.set(index = targetItemIndex, element = updateRecord)
    }
  }


  fun startRecord(customFileName: String = "") {
    viewModelScope.launch {

      val nameFormat = useCaseGetNameFormat().first()

      if (nameFormat == SettingNameFormat.ASK_ON_RECORD && customFileName.isEmpty()) {
        setUiEvent(RecorderScreenUiEvent.NAME_PICKER_DIALOG)
        return@launch
      }

      val savePath = useCaseGetSavePath().first()
      val audioRecordSetting = useCaseGetAudioFormat().first()

      val fileName = customFileName.ifEmpty { convertTimeStampToDate(nameFormat.pattern) }
      val document = useCaseCreateDocumentFile(fileName = fileName, savePath = savePath)

      if (document != null) {
        val fileDescriptor = useCaseGetFileDescriptorByUri(document.uri)
        if (fileDescriptor != null) {
          serviceConnection.mService.startRecord(
            fileDescriptor = fileDescriptor,
            recordAudioSetting = audioRecordSetting,
            fileSaveUri = document.uri,
          )
        }
        fileDescriptor?.close()
      } else {
        setUiEvent(RecorderScreenUiEvent.SAF_PATH)
      }
    }
  }

  fun stopRecord() = serviceConnection.mService.stopRecord()


  fun writeDataStoreSavePath(savePath: String, shouldUpdateList: Boolean = false) = viewModelScope.launch {
    useCaseWriteSavePath(savePath)
    if (shouldUpdateList) getRecords()
  }

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

  fun selectAllItem() = viewModelScope.launch(Dispatchers.Default) {
    recordDataList.forEach {
      if (it !in selectedItemList) {
        viewModelScope.launch {
          selectedItemList.add(it)
        }
      }
    }
  }

  fun deSelectAllItem() = viewModelScope.launch {
    selectedItemList.clear()
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
    val savePath = useCaseGetSavePath().first()
    val documentFile = useCaseGetDocumentTreeFileFromUri(savePath.toUri())

    if (documentFile == null) {
      setUiEvent(RecorderScreenUiEvent.SAF_PATH)
      return@launch
    }

    useCaseGetRecords(documentFile)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), RepositoryResult.Loading)
      .collect { result ->
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

  private fun observeMediaPlayerStateListener() = viewModelScope.launch {
    mediaPlayerServiceConnection.mediaPlayerState.collectLatest {
      mediaPlayerState = it
    }
  }

  fun setUiEvent(event: RecorderScreenUiEvent) = viewModelScope.launch {
    _uiEvent.emit(event)
  }

  private fun initialAudioSetting() = viewModelScope.launch {
    currentAudioFormat = useCaseGetAudioFormat().first()
  }

  private fun observeMediaRecorderService() {
    viewModelScope.launch {
      val isServiceBind = serviceConnection.bindService()
      if (isServiceBind) {
        launch {
          serviceConnection.mService.amplitudes.collect {
            amplitudesList.add(it)
          }
        }
        launch {
          serviceConnection.mService.recordTimer.collectLatest { time ->
            _recordTime.update { time }
          }
        }
        launch {
          serviceConnection.mService.recorderState.collectLatest { recordState ->
            recorderState = recordState
          }
        }
        launch {
          serviceConnection.mService.lastRecord.collect { uri ->
            useCaseGetRecordByUri(uri).await()?.let { recordDataList.add(0, it) }
            if (recordDataList.size != 0) screenRecorderScreenUiState.value = RecorderScreenUiState.DATA
            amplitudesList.clear()
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
