package com.shermanrex.recorderApp.data.service.connection

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.shermanrex.recorderApp.domain.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.data.service.MediaRecorderService
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class MediaRecorderServiceConnection @Inject constructor(
  var context: Context,
) {

  private lateinit var mConnection: ServiceConnection
  lateinit var mService: MediaRecorderService
  private var isBind = false

  suspend fun bindService(): Boolean {
    return suspendCoroutine { continuation ->
      mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
          val binder = service as MediaRecorderService.MyServiceBinder
          mService = binder.getService()
          isBind = true
          continuation.resumeWith(Result.success(true))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          isBind = false
          continuation.resumeWith(Result.success(false))
        }
      }
      context.bindService(
        Intent(context, MediaRecorderService::class.java),
        mConnection,
        BIND_AUTO_CREATE
      )
    }
  }

  fun sendIntentToService(action: ServiceActionNotification) {
    Intent(context, MediaRecorderService::class.java).apply {
      this.action = action.toString()
      context.startService(this)
    }
  }

  fun unBindService() {
    if (isBind) context.unbindService(mConnection)
  }

}