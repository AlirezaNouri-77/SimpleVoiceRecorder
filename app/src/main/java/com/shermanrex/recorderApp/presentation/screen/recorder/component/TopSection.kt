package com.shermanrex.recorderApp.presentation.screen.recorder.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.util.convertHzToKhz
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun TopSection(
  modifier: Modifier = Modifier,
  currentAudioSetting: RecordAudioSetting,
  recorderState: RecorderState,
  amplitudesList: () -> List<Float>,
  recordTime: () -> Int,
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
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Canvas(
        modifier = Modifier
          .height(100.dp)
          .fillMaxWidth(0.95f)
          .background(
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
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
          .wrapContentHeight()
          .padding(horizontal = 15.dp),
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
  }
}

@Preview()
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
      recorderState = RecorderState.RECORDING,
    )
  }
}