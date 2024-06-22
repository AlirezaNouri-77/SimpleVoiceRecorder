package com.shermanrex.recorderApp.presentation.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shermanrex.recorderApp.data.service.MediaRecorderService
import dagger.hilt.android.AndroidEntryPoint

// receive button action on notification
@AndroidEntryPoint
class NotificationActionBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    Intent(context, MediaRecorderService::class.java).apply {
      this.action = intent?.action.toString()
      context?.startService(this)
    }
  }
}