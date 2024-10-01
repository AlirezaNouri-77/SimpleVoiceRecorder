package com.shermanrex.recorderApp.presentation.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shermanrex.recorderApp.data.service.MediaRecorderService

// receive button action on notification
class NotificationActionBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    Intent(context, MediaRecorderService::class.java).apply {
      this.action = intent?.action.toString()
      context?.startService(this)
    }
  }
}