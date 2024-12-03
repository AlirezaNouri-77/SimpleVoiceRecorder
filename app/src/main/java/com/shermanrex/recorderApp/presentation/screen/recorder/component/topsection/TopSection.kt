package com.shermanrex.recorderApp.presentation.screen.recorder.component.topsection

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.screen.recorder.component.topsection.component.TopSectionAmplitudesGraph
import com.shermanrex.recorderApp.presentation.screen.recorder.component.topsection.component.TopSectionTimer
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

// contains amplitudes graph, Timer, record setting
@Composable
fun TopSection(
  modifier: Modifier = Modifier,
  currentAudioSetting: RecordAudioSetting,
  recorderState: RecorderState,
  amplitudesList: () -> List<Float>,
  recordTime: () -> Int,
) {

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary,
    ),
    elevation = CardDefaults.cardElevation(
      defaultElevation = 15.dp
    )
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp),
      verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {

      TopSectionAmplitudesGraph(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(10.dp)
          ),
        recorderState = { recorderState },
        amplitudesList = { amplitudesList() }
      )

      TopSectionTimer(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(),
        currentAudioSetting = currentAudioSetting,
        recordTime = { recordTime() }
      )

    }
  }
}

@Preview()
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  val list = mutableListOf<Float>()
  repeat(700) {
    var item = (1000..28000).random()
    list.add(item.toFloat())
  }
  AppRecorderTheme {
    TopSection(
      currentAudioSetting = RecordAudioSetting(
        AudioFormat.M4A,
        bitrate = 128_000,
        sampleRate = 441_000
      ),
      amplitudesList = { list },
      recordTime = { 0 },
      recorderState = RecorderState.RECORDING,
    )
  }
}