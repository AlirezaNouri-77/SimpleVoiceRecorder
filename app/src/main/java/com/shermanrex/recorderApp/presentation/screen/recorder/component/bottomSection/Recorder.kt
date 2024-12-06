package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component.RecordController
import com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component.AmplitudesGraph
import com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component.RecordTimerAndConfig
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun Recorder(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  amplitudesList: () -> List<Float>,
  currentAudioSetting: RecordAudioSetting,
  recordTime: () -> Int,
  onPauseRecordClick: () -> Unit,
  onStopRecordClick: () -> Unit,
  onStartRecordClick: () -> Unit,
  onResumeRecordClick: () -> Unit,
) {

  Column(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    AmplitudesGraph(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(start = 15.dp, end =15.dp, top = 4.dp, bottom = 2.dp),
      recorderState = { recorderState() },
      amplitudesList = { amplitudesList() }
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp, bottom = 7.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      RecordTimerAndConfig(
        modifier = Modifier.weight(0.8f),
        currentAudioSetting = currentAudioSetting,
        recordTime = { recordTime() }
      )
      RecordController(
        modifier = Modifier,
        recorderState = { recorderState() },
        onPauseRecordClick = { onPauseRecordClick() },
        onStartRecordClick = { onStartRecordClick() },
        onStopRecordClick = { onStopRecordClick() },
        onResumeRecordClick = { onResumeRecordClick() },
      )
    }
  }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  val list = mutableListOf<Float>()
  repeat(700) {
    var item = (1000..28000).random()
    list.add(item.toFloat())
  }
  AppRecorderTheme {
    Box(
      modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ){
      Recorder(
        recorderState = { RecorderState.IDLE },
        amplitudesList = { list },
        currentAudioSetting = RecordAudioSetting(
          AudioFormat.M4A,
          bitrate = 128_000,
          sampleRate = 441_000
        ),
        recordTime = { 0 },
        onPauseRecordClick = {},
        onStopRecordClick = {},
        onStartRecordClick = {},
        onResumeRecordClick = {},
      )
    }
  }
}
