package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.util.convertHzToKhz
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting

@Composable
fun RecordTimerAndConfig(
  modifier: Modifier = Modifier,
  currentAudioSetting: RecordAudioSetting,
  recordTime: () -> Int,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
  ) {
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = recordTime().convertMilliSecondToTime(),
      fontSize = 40.sp,
      textAlign = TextAlign.Start,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onPrimary,
    )
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = "${currentAudioSetting.format.name.uppercase()}," +
           " ${currentAudioSetting.bitrate.convertToKbps()}," +
           " ${currentAudioSetting.sampleRate.convertHzToKhz()}",
      fontSize = 12.sp,
      textAlign = TextAlign.Start,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onPrimary,
    )
  }
}