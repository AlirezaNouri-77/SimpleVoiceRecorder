package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shermanrex.recorderApp.data.Constant
import com.shermanrex.recorderApp.presentation.util.getActivity
import com.shermanrex.recorderApp.presentation.util.openSetting

@Composable
fun CheckMicrophonePermission(
  context: Context,
  onGrant: () -> Unit,
  onDenied: () -> Unit,
) {

  if (Constant.permissionList.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
    onGrant()
    return
  }

  val permissionList = remember { mutableListOf<String>() }

  Constant.permissionList.forEach {
    if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED && !permissionList.contains(it)) {
      permissionList.add(it)
    }
  }

  var microphonePermission by remember {
    mutableStateOf(false)
  }
  var notificationPermission by remember {
    mutableStateOf(false)
  }

  LaunchedEffect(notificationPermission, microphonePermission) {
    if (notificationPermission && microphonePermission) onGrant()
  }

  permissionList.onEach {

    val microphoneActivityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGrant ->
      when (isGrant) {
        true -> permissionList.remove(it)
        false -> onDenied()
      }
    }

    when {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED -> {
        if (it == Manifest.permission.RECORD_AUDIO) {
          microphonePermission = true
        } else notificationPermission = true
      }

      context.getActivity()?.let { it1 -> ActivityCompat.shouldShowRequestPermissionRationale(it1, it) } == true -> {
        val dialogText = when (it) {
          Manifest.permission.RECORD_AUDIO -> "The microphone permission isn't granted"
          Manifest.permission.POST_NOTIFICATIONS -> "The notification permission isn't granted"
          else -> ""
        }
        AlertDialog(
          title = {
            Text(text = "permission")
          },
          text = {
            Text(text = dialogText)
          },
          onDismissRequest = {
            onDenied()
          },
          dismissButton = {
            Button(
              onClick = {
                onDenied()
              }
            ) { Text("Dismiss") }
          },
          confirmButton = {
            Button(
              onClick = context::openSetting
            ) { Text("Open Setting") }
          },
          containerColor = MaterialTheme.colorScheme.primary
        )
      }

      context.getActivity()?.let { it1 -> ActivityCompat.shouldShowRequestPermissionRationale(it1, it) } == false -> {
        SideEffect { microphoneActivityResult.launch(it) }
      }

      else -> SideEffect { microphoneActivityResult.launch(it) }

    }
  }
}