package com.shermanrex.recorderApp.data.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.shermanrex.recorderApp.data.di.annotation.ServiceModuleQualifier
import com.shermanrex.recorderApp.domain.model.notification.NotificationActions
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetAudioFormat
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseAppendFileExtension
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetDocumentFileFromUri
import com.shermanrex.recorderApp.presentation.notification.MyNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaRecorderService : LifecycleService() {

  private var binder = MyServiceBinder()

  @Inject
  lateinit var myNotificationManager: MyNotificationManager

  @Inject
  lateinit var mediaRecorder: MediaRecorder

  @Inject
  @ServiceModuleQualifier
  lateinit var useCaseFileAppendFileExtension: UseCaseAppendFileExtension

  @Inject
  @ServiceModuleQualifier
  lateinit var useCaseGetDocumentFileFromUri: UseCaseGetDocumentFileFromUri

  @Inject
  @ServiceModuleQualifier
  lateinit var useCaseGetAudioFormat: UseCaseGetAudioFormat

  private var currentRecordFileUri: Uri = Uri.EMPTY
  private var startRecordTimeStamp = 0L
  private var currentTimer = 0

  // when the record is stop this Shareflow send the last record uri
  private var _lastRecord = MutableSharedFlow<Uri>()
  val lastRecord = _lastRecord.asSharedFlow()

  private var _recorderState = MutableStateFlow(RecorderState.IDLE)
  val recorderState = _recorderState.asStateFlow()

  private var _recordTimer = MutableStateFlow(0)
  var recordTimer = _recordTimer.asStateFlow()

  var amplitudes = flow {
    while (currentCoroutineContext().isActive) {
      delay(100)
      if (_recorderState.value == RecorderState.RECORDING) {
        emit(mediaRecorder.maxAmplitude.toFloat())
      }
    }
  }

  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    return binder
  }

  override fun onCreate() {
    super.onCreate()
    lifecycleScope.launch {
      while (this.isActive) {
        delay(100)
        if (_recorderState.value == RecorderState.RECORDING) {
          val timeRecord = (System.currentTimeMillis() - startRecordTimeStamp).toInt() + currentTimer
          _recordTimer.value = timeRecord
        }
      }
    }
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    if (_recorderState.value != RecorderState.RECORDING) {
      stopForeground(STOP_FOREGROUND_REMOVE)
      stopSelf()
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    when (intent?.action) {
      NotificationActions.STOP.name -> stopRecord()
      NotificationActions.PAUSE.name -> pauseRecord()
      NotificationActions.RESUME.name -> resumeRecord()
    }
    return START_STICKY
  }

  fun startRecord(
    fileDescriptor: ParcelFileDescriptor,
    recordAudioSetting: RecordAudioSetting,
    fileSaveUri: Uri,
  ) {
    if (recorderState.value == RecorderState.PAUSE) {
      mediaRecorder.resume()
      return
    }
    mediaRecorder.apply {
      setAudioSource(MediaRecorder.AudioSource.MIC)
      setOutputFormat(recordAudioSetting.format.outputFormat)
      setAudioEncoder(recordAudioSetting.format.audioEncoder)
      setAudioEncodingBitRate(recordAudioSetting.bitrate)
      setAudioSamplingRate(recordAudioSetting.sampleRate)
      setOutputFile(fileDescriptor.fileDescriptor)
      this.maxAmplitude
      prepare()
      start()
    }
    _recorderState.value = RecorderState.RECORDING
    setServiceToForeground()
    currentRecordFileUri = fileSaveUri
    startRecordTimeStamp = System.currentTimeMillis()
    fileDescriptor.close()
    myNotificationManager.updateNotificationState(notificationActions = NotificationActions.RECORDING)
  }

  fun stopRecord() {
    lifecycleScope.launch {
      if (recorderState.value == RecorderState.IDLE) return@launch
      val recordedFileName = useCaseGetDocumentFileFromUri(currentRecordFileUri)?.name ?: "Not Found"
      mediaRecorder.apply {
        stop()
        reset()
      }
      _recorderState.value = RecorderState.IDLE
      stopForeground(STOP_FOREGROUND_DETACH)
      myNotificationManager.updateNotificationState(notificationActions = NotificationActions.STOP, recordName = recordedFileName)
      _recordTimer.value = 0
      currentTimer = 0
      val format = useCaseGetAudioFormat().first().format.name
      val newUri = useCaseFileAppendFileExtension(currentRecordFileUri, format)
      _lastRecord.emit(newUri)
    }
  }

  fun resumeRecord() {
    mediaRecorder.resume()
    startRecordTimeStamp = System.currentTimeMillis()
    _recorderState.value = RecorderState.RECORDING
    myNotificationManager.updateNotificationState(notificationActions = NotificationActions.PAUSE)
  }

  fun pauseRecord() {
    if (recorderState.value == RecorderState.RECORDING) {
      myNotificationManager.updateNotificationState(notificationActions = NotificationActions.RESUME)
      mediaRecorder.pause()
      currentTimer = _recordTimer.value
      _recorderState.value = RecorderState.PAUSE
    }
  }

  private fun setServiceToForeground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ServiceCompat.startForeground(
        this@MediaRecorderService,
        NOTIFICATION_ID,
        myNotificationManager.notificationBuilder.build(),
        FOREGROUND_SERVICE_TYPE,
      )
    } else {
      startForeground(
        NOTIFICATION_ID,
        myNotificationManager.notificationBuilder.build(),
      )
    }
  }

  inner class MyServiceBinder : Binder() {
    fun getService() = this@MediaRecorderService
  }

  companion object {
    const val NOTIFICATION_ID = 1
    val FOREGROUND_SERVICE_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
    } else {
      0
    }
  }

}
