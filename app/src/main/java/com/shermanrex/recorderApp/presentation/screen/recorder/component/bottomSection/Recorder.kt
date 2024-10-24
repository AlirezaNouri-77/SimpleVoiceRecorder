package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shermanrex.presentation.screen.component.util.NoRipple
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.data.Constant
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.screen.component.util.bounce
import com.shermanrex.recorderApp.presentation.screen.component.util.getActivity
import com.shermanrex.recorderApp.presentation.screen.component.util.openSetting

@Composable
fun Recorder(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  onPauseRecordClick: () -> Unit,
  onStopRecordClick: () -> Unit,
  onStartRecordClick: () -> Unit,
  onResumeRecordClick: () -> Unit,
  context: Context = LocalContext.current,
) {

  val isDarkMode = isSystemInDarkTheme()
  var onButtonClick by remember {
    mutableStateOf(false)
  }
  val animateButtonColor = remember {
    Animatable(if (isDarkMode) Color.White else Color.Black)
  }
  var recordButtonColor by remember {
    mutableStateOf(if (isDarkMode) Color.Black else Color.White)
  }
  LaunchedEffect(recorderState()) {
    when (recorderState()) {
      RecorderState.RECORDING -> {
        recordButtonColor = Color.White
        animateButtonColor.animateTo(Color(0xFFED0909), tween(300))
      }
      RecorderState.IDLE -> {
        recordButtonColor = if (isDarkMode) Color.Black else Color.White
        animateButtonColor.animateTo(if (isDarkMode) Color.White else Color.Black, tween(300))
      }
      RecorderState.PAUSE -> {
        recordButtonColor = Color.White
        animateButtonColor.animateTo(Color(0xFF1F75FF), tween(300))
      }
    }
  }
  val buttonText = remember(recorderState()) {
    when (recorderState()) {
      RecorderState.RECORDING -> "Recording"
      RecorderState.IDLE -> "Record"
      RecorderState.PAUSE -> "Resume"
    }
  }

  if (onButtonClick) {
    CheckMicrophonePermission(
      context = context,
      onGrant = {
        onStartRecordClick()
        onButtonClick = false
      },
      onDenied = {
        onButtonClick = false
      }
    )
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally),
  ) {

    IconButton(
      modifier = Modifier
        .bounce()
        .weight(0.2f, false),
      onClick = {
        onPauseRecordClick()
      },
      interactionSource = NoRipple,
      colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    ) {
      Icon(
        modifier = Modifier.size(19.dp),
        painter = painterResource(id = R.drawable.pause),
        contentDescription = "",
      )
    }

    ElevatedButton(
      modifier = Modifier.weight(0.5f, true),
      onClick = {
        if (recorderState() == RecorderState.PAUSE) {
          onResumeRecordClick()
        } else {
          onButtonClick = true
        }
      },
      colors = ButtonDefaults.elevatedButtonColors(
        containerColor = animateButtonColor.value,
      ),
      elevation = ButtonDefaults.elevatedButtonElevation(
        defaultElevation = 10.dp,
        pressedElevation = 5.dp
      )
    ) {
      Text(
        text = buttonText,
        fontWeight = FontWeight.SemiBold,
        color = recordButtonColor,
        fontSize = 19.sp,
      )
    }

    IconButton(
      modifier = Modifier
        .bounce()
        .weight(0.2f, false),
      onClick = {
        onStopRecordClick()
      },
      interactionSource = NoRipple,
      colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    ) {
      Icon(
        modifier = Modifier.size(19.dp),
        painter = painterResource(id = R.drawable.stop),
        contentDescription = "",
      )
    }
  }
}

@Composable
private fun CheckMicrophonePermission(
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


