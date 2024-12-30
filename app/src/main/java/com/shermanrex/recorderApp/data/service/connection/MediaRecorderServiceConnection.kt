package com.shermanrex.recorderApp.data.service.connection

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.shermanrex.recorderApp.data.service.MediaRecorderService
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class MediaRecorderServiceConnection @Inject constructor(
  var context: Context,
) {

  private lateinit var mConnection: ServiceConnection
  lateinit var mService: MediaRecorderService
  private var isBind = AtomicBoolean(false)

  suspend fun bindService(): Boolean {
    return suspendCoroutine { continuation ->
      mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
          val binder = service as MediaRecorderService.MyServiceBinder
          mService = binder.getService()
          isBind.set(true)
          continuation.resumeWith(Result.success(true))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          isBind.set(false)
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

  fun unBindService() {
    if (isBind.get()) {
      isBind.set(false)
      context.unbindService(mConnection)
    }
  }

}