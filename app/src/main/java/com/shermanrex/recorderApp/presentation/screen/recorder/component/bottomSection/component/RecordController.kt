package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import com.shermanrex.recorderApp.presentation.ui.theme.BlueColor
import com.shermanrex.recorderApp.presentation.util.bounce

@Composable
fun RecordController(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  onPauseRecordClick: () -> Unit,
  onStartRecordClick: () -> Unit,
  onStopRecordClick: () -> Unit,
  onResumeRecordClick: () -> Unit,
  context: Context = LocalContext.current,
) {

  var onPrimary = MaterialTheme.colorScheme.onPrimary

  var onButtonClick by remember {
    mutableStateOf(false)
  }

  var recordButtonColor by remember {
    mutableStateOf(Color.Red)
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

  LaunchedEffect(recorderState()) {
    recordButtonColor = when (recorderState()) {
      RecorderState.RECORDING -> onPrimary
      RecorderState.IDLE -> Color.Red
      RecorderState.PAUSE -> BlueColor
    }
  }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
  ) {
    IconButton(
      modifier = Modifier.bounce(),
      onClick = {
        onStopRecordClick()
      },
      colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    ) {
      Icon(
        modifier = Modifier
          .size(30.dp),
        painter = painterResource(id = R.drawable.icon_record_stop),
        contentDescription = "",
      )
    }
    IconButton(
      modifier = Modifier
        .size(48.dp)
        .bounce(),
      onClick = {
        when (recorderState()) {
          RecorderState.RECORDING -> onPauseRecordClick()
          RecorderState.IDLE -> onButtonClick = true
          RecorderState.PAUSE -> onResumeRecordClick()
        }
      },
      colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = recordButtonColor,
      ),
    ) {
      Icon(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(
          id = when (recorderState()) {
            RecorderState.RECORDING -> R.drawable.icon_record_resume
            RecorderState.IDLE -> R.drawable.icon_record_record
            RecorderState.PAUSE -> R.drawable.icon_record_play
          }
        ),
        contentDescription = "",
      )
    }
  }

}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  AppRecorderTheme {
    Column(
      modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
      RecordController(
        recorderState = { RecorderState.RECORDING },
        onPauseRecordClick = {},
        onStartRecordClick = {},
        onStopRecordClick = {},
        onResumeRecordClick = {},
      )
      RecordController(
        recorderState = { RecorderState.IDLE },
        onPauseRecordClick = {},
        onStartRecordClick = {},
        onStopRecordClick = {},
        onResumeRecordClick = {},
      )
      RecordController(
        recorderState = { RecorderState.PAUSE },
        onPauseRecordClick = {},
        onStartRecordClick = {},
        onStopRecordClick = {},
        onResumeRecordClick = {},
      )
    }
  }
}
