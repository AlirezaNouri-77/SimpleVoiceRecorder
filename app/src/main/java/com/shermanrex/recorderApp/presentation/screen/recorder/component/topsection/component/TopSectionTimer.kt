package com.shermanrex.recorderApp.presentation.screen.recorder.component.topsection.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.util.convertHzToKhz
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting

@Composable
fun TopSectionTimer(
  modifier: Modifier = Modifier,
  currentAudioSetting: RecordAudioSetting,
  recordTime: () -> Int,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = recordTime().convertMilliSecondToTime(),
      fontSize = 38.sp,
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onPrimary,
    )
    Text(
      text = "${currentAudioSetting.format.name.uppercase()}," +
           " ${currentAudioSetting.bitrate.convertToKbps()}," +
           " ${currentAudioSetting.sampleRate.convertHzToKhz()}",
      fontSize = 12.sp,
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onPrimary,
    )
  }
}