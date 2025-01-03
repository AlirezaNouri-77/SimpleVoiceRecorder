package com.shermanrex.recorderApp.presentation.screen.recorder

import android.net.Uri
import androidx.compose.runtime.getValue
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
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.domain.model.record.SettingNameFormat
import com.shermanrex.recorderApp.domain.model.repository.RepositoryResult
import com.shermanrex.recorderApp.domain.model.ui.DropDownMenuStateUi
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiEvent
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
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

  var isLoading by mutableStateOf(false)

  var recordDataList = mutableStateListOf<RecordModel>()
  var amplitudesList = mutableStateListOf<Float>()

  var selectedItemList = mutableStateListOf<RecordModel>()

  var currentAudioFormat by mutableStateOf(RecordAudioSetting.Empty)

  var dropDownMenuState by mutableStateOf(DropDownMenuStateUi.Empty)
  var showSettingBottomSheet by mutableStateOf(false)

  var showSelectMode by mutableStateOf(false)
  var currentItemIndex by mutableIntStateOf(-1)

  private var _uiEvent = MutableSharedFlow<RecorderScreenUiEvent>(replay = 1)
  var uiEvent = _uiEvent.asSharedFlow()

  private var _recordTime = MutableStateFlow(0)
  var recordTime = _recordTime.asStateFlow()

  private var _recordState = MutableStateFlow(RecorderState.IDLE)
  var recorderState = _recordState.asStateFlow()

  private var _mediaPlayerPosition = MutableStateFlow(0L)
  var mediaPlayerPosition = _mediaPlayerPosition
    .onStart {
      observePlayerPosition()
    }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      0
    )

  var mediaPlayerState = mediaPlayerServiceConnection.mediaPlayerState
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5_000L),
      CurrentMediaPlayerState.Empty
    )

  init {
    getRecords()
    initialAudioSetting()
    observeMediaRecorderService()
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
      if (mediaPlayerState.value.isPlaying) stopPlayAudio()
      if (useCaseDeleteRecord(recordModel.path)) {
        recordDataList.remove(recordModel)
      }
    }
  }

  fun deleteRecord() {
    if (selectedItemList.isEmpty()) return
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

  fun stopRecord() {
    serviceConnection.mService.stopRecord()
    amplitudesList.clear()
  }


  fun writeDataStoreSavePath(savePath: String, shouldUpdateList: Boolean = false) = viewModelScope.launch {
    useCaseWriteSavePath(savePath)
    if (shouldUpdateList) getRecords()
  }

  fun pauseRecord() {
    if (recorderState.value == RecorderState.RECORDING) {
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
    _mediaPlayerPosition.update { position.toLong() }
    mediaPlayerServiceConnection.seekToPosition(position = position)
  }

  private fun getRecords() = viewModelScope.launch {
    val savePath = useCaseGetSavePath().first()
    val documentFile = useCaseGetDocumentTreeFileFromUri(savePath.toUri())

    if (documentFile == null) {
      setUiEvent(RecorderScreenUiEvent.SAF_PATH)
      return@launch
    }

    useCaseGetRecords(documentFile)
      .collect { result ->
        when (result) {
          is RepositoryResult.Success -> {
            recordDataList.clear()
            recordDataList.addAll(result.data)
            isLoading = false
          }

          is RepositoryResult.Failure -> isLoading = false
          RepositoryResult.Loading -> isLoading = true
        }
      }
  }

  fun setUiEvent(event: RecorderScreenUiEvent) = viewModelScope.launch {
    _uiEvent.emit(event)
  }

  private fun initialAudioSetting() = viewModelScope.launch {
    currentAudioFormat = useCaseGetAudioFormat().first()
  }

  private fun observePlayerPosition() {
    viewModelScope.launch {
      mediaPlayerServiceConnection.mediaPlayerPosition.collect {
        _mediaPlayerPosition.value = it
      }
    }
  }

  private fun observeMediaRecorderService() {
    viewModelScope.launch {
      val isServiceBind = serviceConnection.bindService()
      if (isServiceBind) {
        launch {
          serviceConnection.mService.amplitudes.shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            0,
          ).collect {
            if (amplitudesList.size > 1000) {
              amplitudesList.removeAt(amplitudesList.lastIndex)
            }
            amplitudesList.add(index = 0, it)
          }
        }
        launch {
          serviceConnection.mService.recorderTimer.collectLatest { time ->
            _recordTime.update { time }
          }
        }
        launch {
          serviceConnection.mService.recorderState.collectLatest { recordState ->
            _recordState.update { recordState }
          }
        }
        launch {
          serviceConnection.mService.lastRecord.collect { uri ->
            useCaseGetRecordByUri(uri)?.let { recordDataList.add(0, it) }
          }
        }
      }
    }
  }

}
