package com.shermanrex.recorderApp.presentation.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.data.service.MediaRecorderService
import com.shermanrex.recorderApp.domain.model.notification.NotificationActions
import com.shermanrex.recorderApp.presentation.broadcastReceiver.NotificationActionBroadcastReceiver

class MyNotificationManager(private var context: Context) {

  private val notificationManager: NotificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setPriority(NotificationManager.IMPORTANCE_MAX)
    .setSilent(true)
    .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)

  private val stopPendingIntent = Intent(context.applicationContext, NotificationActionBroadcastReceiver::class.java)
    .run {
      action = NotificationActions.STOP.name
      PendingIntent.getBroadcast(context.applicationContext, 1, this, PendingIntent.FLAG_IMMUTABLE)
    }

  fun updateNotificationState(notificationActions: NotificationActions, recordName: String = "") {
    when (notificationActions) {
      NotificationActions.RECORDING -> setNotificationActionToPause()
      NotificationActions.PAUSE -> setNotificationActionToPause()
      NotificationActions.STOP -> setRecordEndedNotification(recordName)
      NotificationActions.RESUME -> setNotificationActionToResume()
      NotificationActions.IDLE -> {}
    }
  }

  private fun setRecordEndedNotification(name: String) {
    val notification = notificationBuilder.clearActions().setContentText("$name Recorded").build()
    notificationManager.notify(MediaRecorderService.NOTIFICATION_ID, notification)
  }

  private fun setNotificationActionToResume() {
    val resumePendingIntent = Intent(context.applicationContext, NotificationActionBroadcastReceiver::class.java)
      .run {
        action = NotificationActions.RESUME.toString()
        PendingIntent.getBroadcast(context.applicationContext, 2, this, PendingIntent.FLAG_IMMUTABLE)
      }
    notificationBuilder.run {
      clearActions()
      setSmallIcon(R.drawable.ic_launcher_foreground)
      addAction(R.drawable.icon_waveform, "Resume", resumePendingIntent)
      addAction(R.drawable.stop, "Stop", stopPendingIntent)
      setContentText("Record Pause")
      build()
    }.also { notificationManager.notify(MediaRecorderService.NOTIFICATION_ID, it) }
  }

  private fun setNotificationActionToPause() {
    val resumePendingIntent = Intent(context.applicationContext, NotificationActionBroadcastReceiver::class.java)
      .run {
        action = NotificationActions.PAUSE.toString()
        PendingIntent.getBroadcast(context.applicationContext, 2, this, PendingIntent.FLAG_IMMUTABLE)
      }
    notificationBuilder.run {
      clearActions()
      setSmallIcon(R.drawable.ic_launcher_foreground)
      addAction(R.drawable.pause, "Pause", resumePendingIntent)
      addAction(R.drawable.stop, "Stop", stopPendingIntent)
      setContentText("Recording")
      build()
    }.also { notificationManager.notify(MediaRecorderService.NOTIFICATION_ID, it) }
  }

  companion object {
    const val CHANNEL_ID = "AppRecorder"
  }

}