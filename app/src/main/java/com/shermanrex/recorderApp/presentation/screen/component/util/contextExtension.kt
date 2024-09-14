package com.shermanrex.recorderApp.presentation.screen.component.util

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity

fun Context.getActivity(): ComponentActivity? = when (this) {
  is ComponentActivity -> this
  is ContextWrapper -> baseContext.getActivity()
  else -> null
}

fun Context.openSetting() {
  Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    Uri.fromParts("package", this.packageName, null)
  ).also { intent ->
    this.startActivity(intent)
  }
}

fun Context.shareItem(uri:Uri) {
  Intent().apply {
    setAction(Intent.ACTION_SEND)
    putExtra(Intent.EXTRA_STREAM, uri)
    setType("audio/*")
  }.also {
    this.startActivity(it)
  }
}