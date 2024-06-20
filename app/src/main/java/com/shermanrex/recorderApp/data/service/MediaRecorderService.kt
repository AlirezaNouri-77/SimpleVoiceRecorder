package com.shermanrex.recorderApp.data.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.shermanrex.presentation.notification.MyNotificationManager
import com.shermanrex.recorderApp.data.model.RecordAudioSetting
import com.shermanrex.recorderApp.data.model.RecorderState
import com.shermanrex.recorderApp.data.model.notification.ServiceActionNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaRecorderService : LifecycleService() {

  private var mediaRecorder: MediaRecorder? = null

  private var binder = MyServiceBinder()

  private lateinit var myNotificationManager: MyNotificationManager

  private var _recorderState = MutableStateFlow(RecorderState.IDLE)
  val recorderState = _recorderState.asStateFlow()

  private var _recordTimer = MutableStateFlow(0)
  var recordTimer = _recordTimer.asStateFlow()

  private var _amplitudes = MutableStateFlow(0f)
  val amplitudes = _amplitudes.asStateFlow()

  init {
    mediaRecorder?.maxAmplitude
    lifecycleScope.launch {
      while (this.isActive) {
        delay(100)
        if (_recorderState.value == RecorderState.RECORDING) {
          _recordTimer.value += 100
          _amplitudes.emit(mediaRecorder?.maxAmplitude?.toFloat() ?: 0f)
        }
      }
    }
  }

  override fun onBind(intent: Intent): IBinder {
    super.onBind(intent)
    return binder
  }

  override fun onCreate() {
    super.onCreate()
    myNotificationManager = MyNotificationManager(this)
    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      MediaRecorder(this)
    } else {
      MediaRecorder()
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
      ServiceActionNotification.STOP.toString() -> {
        stopRecord()
        stopForeground(STOP_FOREGROUND_DETACH)
      }

      ServiceActionNotification.PAUSE.toString() -> pauseRecord()

      ServiceActionNotification.START.toString() -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          ServiceCompat.startForeground(
            this@MediaRecorderService,
            NOTIFICATION_ID,
            myNotificationManager.notification.build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            } else {
              0
            }
          )
        } else {
          startForeground(
            NOTIFICATION_ID,
            myNotificationManager.notification.build(),
          )
        }
      }

      ServiceActionNotification.RESUME.toString() -> resumeRecord()

    }
    return START_STICKY
  }

  fun startRecord(fileDescriptor: ParcelFileDescriptor, recordAudioSetting: RecordAudioSetting) {
    if (recorderState.value == RecorderState.PAUSE) {
      mediaRecorder?.resume()
      return
    }
    mediaRecorder?.apply {
      setAudioSource(MediaRecorder.AudioSource.MIC)
      setOutputFormat(recordAudioSetting.format.outputFormat)
      setAudioEncoder(recordAudioSetting.format.audioEncoder)
      setAudioEncodingBitRate(recordAudioSetting.bitrate)
      setAudioSamplingRate(recordAudioSetting.sampleRate)
      setOutputFile(fileDescriptor.fileDescriptor)
      prepare()
      start()
    }.also {
      setRecordState(RecorderState.RECORDING)
    }
    fileDescriptor.close()
    myNotificationManager.updatePauseAndResumeNotification(
      recorderState = _recorderState.value,
    )
  }

  fun stopRecord() {
    if (recorderState.value == RecorderState.RECORDING || recorderState.value == RecorderState.PAUSE) {
      setRecordState(RecorderState.STOP)
      mediaRecorder?.stop()
      mediaRecorder?.reset()
      myNotificationManager.setStopRecordNotification(_recordTimer.value)
      _recordTimer.value = 0
    }
  }

  fun resumeRecord() {
    mediaRecorder?.resume()
    setRecordState(RecorderState.RECORDING)
    myNotificationManager.updatePauseAndResumeNotification(
      recorderState = _recorderState.value,
    )
  }

  fun pauseRecord() {
    if (recorderState.value == RecorderState.RECORDING) {
      mediaRecorder?.pause()
    }
    setRecordState(RecorderState.PAUSE)
    myNotificationManager.updatePauseAndResumeNotification(
      recorderState = _recorderState.value,
    )
  }

  private fun setRecordState(recordState: RecorderState) {
    _recorderState.value = recordState
  }

  inner class MyServiceBinder : Binder() {
    fun getService() = this@MediaRecorderService
  }

  companion object {
    const val NOTIFICATION_ID = 1
  }

}
