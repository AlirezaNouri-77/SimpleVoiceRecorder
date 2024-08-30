package com.shermanrex.recorderApp.presentation.screen.recorder.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.presentation.screen.component.util.NoRipple
import com.shermanrex.presentation.screen.component.util.bounce
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.domain.model.AudioFormat
import com.shermanrex.recorderApp.domain.model.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.RecorderState
import com.shermanrex.recorderApp.data.util.bitToKbps
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKhz
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun TopSection(
  modifier: Modifier = Modifier,
  currentAudioSetting: RecordAudioSetting,
  recorderState: RecorderState,
  amplitudesList: () -> List<Float>,
  recordTime: () -> Int,
  onSettingClick: () -> Unit,
) {

  val canvasColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
  var reCanvas by remember {
    mutableIntStateOf(0)
  }
  LaunchedEffect(amplitudesList().size) {
    ++reCanvas
  }

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(bottomEnd = 45.dp, bottomStart = 45.dp),
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
        .padding(10.dp)
        .statusBarsPadding(),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      horizontalAlignment = Alignment.Start,
    ) {

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "App Recorder",
          modifier = Modifier,
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp,
          color = MaterialTheme.colorScheme.onPrimary,
        )
        IconButton(
          modifier = Modifier
            .bounce()
            .weight(0.1f, false),
          interactionSource = NoRipple,
          onClick = { onSettingClick() },
        ) {
          Icon(
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "",
            modifier = Modifier.size(
              24.dp
            ),
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        }
      }

      Canvas(
        modifier = Modifier
          .height(120.dp)
          .fillMaxWidth()
          .background(
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(10.dp)
          ),
      ) {

        val canvasSize = this.size
        val spikeWidth = 6f
        val spikeSpace = 8f

        val spikeCount = ((canvasSize.width - ((spikeSpace))) / (spikeWidth + spikeSpace)).toInt()
        val list = amplitudesList().takeLast(spikeCount).asReversed()

        // draw vertical center line
        drawLine(
          color = canvasColor,
          strokeWidth = 3f,
          alpha = 0.4f,
          cap = StrokeCap.Round,
          start = Offset(
            x = 5f,
            y = canvasSize.height / 2f
          ),
          end = Offset(
            x = this.size.width - 5f,
            y = canvasSize.height / 2f
          ),
        )

        reCanvas.apply {
          if (recorderState == RecorderState.RECORDING || recorderState == RecorderState.PAUSE) {
            list.forEachIndexed { index, item ->

              val spikeHeight = item.div(60).coerceAtMost((canvasSize.height / 2) - 30)

              drawLine(
                color = canvasColor,
                strokeWidth = spikeWidth,
                cap = StrokeCap.Round,
                start = Offset(
                  x = (canvasSize.width - spikeWidth - spikeSpace) - (index * (spikeWidth + spikeSpace)),
                  y = canvasSize.height / 2f + spikeHeight
                ),
                end = Offset(
                  x = (canvasSize.width - spikeWidth - spikeSpace) - (index * (spikeWidth + spikeSpace)),
                  y = canvasSize.height / 2f - spikeHeight
                ),
              )

            }
          }
        }

      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = recordTime().convertMilliSecondToTime(),
          fontSize = 44.sp,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
          text = "${currentAudioSetting.format.name.uppercase()}," +
               " ${currentAudioSetting.bitrate.bitToKbps()}," +
               " ${currentAudioSetting.sampleRate.convertToKhz()}",
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      }

    }
  }
}

@Preview(widthDp = 943)
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  val list = mutableListOf<Float>()
  repeat(400) {
    list.add(30000f)
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
      onSettingClick = {},
      recorderState = RecorderState.RECORDING,
    )
  }
}