package com.shermanrex.recorderApp.data

import android.Manifest
import android.os.Build

object Constant {

  const val METADATA_DURATION_KEY = "duration"
  const val METADATA_URI_KEY = "uriPath"

  var permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.POST_NOTIFICATIONS,
    )
  } else {
    arrayOf(
      Manifest.permission.RECORD_AUDIO,
    )
  }

}