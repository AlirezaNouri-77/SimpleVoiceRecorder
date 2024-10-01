package com.shermanrex.recorderApp.presentation.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.data.service.MediaRecorderService
import com.shermanrex.recorderApp.domain.model.RecorderState
import com.shermanrex.recorderApp.domain.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.presentation.broadcastReceiver.NotificationActionBroadcastReceiver

class MyNotificationManager(private var context: Context) {

  private val notificationManager: NotificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  val notification = NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setPriority(NotificationManager.IMPORTANCE_MAX)
    .setSilent(true)
    .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)

  fun setStopRecordNotification(name: String) {
    val notification = notification.clearActions().setContentText("$name Recorded").build()
    notificationManager.notify(MediaRecorderService.NOTIFICATION_ID, notification)
  }

  fun updatePauseAndResumeNotification(recorderState: RecorderState) {

    var intentText: String
    var notificationText: String
    val stopIntent =
      Intent(context.applicationContext, NotificationActionBroadcastReceiver::class.java).apply {
        action = ServiceActionNotification.STOP.toString()
      }
    val intent =
      Intent(context.applicationContext, NotificationActionBroadcastReceiver::class.java).apply {
        action = if (recorderState == RecorderState.RECORDING) {
          notificationText = "Recording"
          intentText = "Pause"
          ServiceActionNotification.PAUSE.toString()
        } else {
          notificationText = "Pause"
          intentText = "Resume"
          ServiceActionNotification.RESUME.toString()
        }
      }

    val stopIntentPending =
      PendingIntent.getBroadcast(context, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE)

    val pauseIntentPending =
      PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = notification.run {
      clearActions()
      setChronometerCountDown(true)
      addAction(R.drawable.stop, intentText, pauseIntentPending)
      addAction(R.drawable.ic_launcher_foreground, "Stop", stopIntentPending)
      setContentText(notificationText)
      build()
    }
    notificationManager.notify(MediaRecorderService.NOTIFICATION_ID, notification)
  }

  companion object {
    var CHANNEL_ID = "AppRecorder"
  }

}