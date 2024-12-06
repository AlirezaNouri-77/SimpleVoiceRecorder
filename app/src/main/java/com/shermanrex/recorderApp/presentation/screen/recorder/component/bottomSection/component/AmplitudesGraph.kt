package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.component

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import kotlin.math.roundToInt

@Composable
fun AmplitudesGraph(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  amplitudesList: () -> List<Float>,
) {

  val canvasColor = MaterialTheme.colorScheme.onPrimary

  var reCanvas by remember {
    mutableIntStateOf(0)
  }
  LaunchedEffect(amplitudesList().size) {
    ++reCanvas
  }

  Box(
    modifier = modifier
      .background(
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp)
      ),
  ) {
    Canvas(
      modifier = Modifier
        .height(60.dp)
        .fillMaxWidth()
        .padding(5.dp),
    ) {

      val canvasSize = this.size
      val spikeWidth = 6f
      val spikeSpace = 4f

      val spikeCount = ((canvasSize.width) / (spikeWidth + spikeSpace)).roundToInt()
      val list = amplitudesList().take(spikeCount)

      // draw vertical center line
      drawLine(
        color = canvasColor,
        strokeWidth = 3f,
        alpha = 0.2f,
        cap = StrokeCap.Round,
        start = Offset(
          x = 0f,
          y = canvasSize.height / 2f
        ),
        end = Offset(
          x = this.size.width,
          y = canvasSize.height / 2f
        ),
      )

      // draw amplitudes
      reCanvas.apply {
        if (recorderState() == RecorderState.RECORDING || recorderState() == RecorderState.PAUSE) {
          list.forEachIndexed { index, item ->

            val spikeHeight = item.div(60).coerceAtMost((canvasSize.height / 2) - 15)

            drawLine(
              color = canvasColor,
              alpha = 0.4f,
              strokeWidth = spikeWidth,
              cap = StrokeCap.Round,
              start = Offset(
                x = (canvasSize.width - (spikeWidth + 1)) - (index * (spikeWidth + spikeSpace)),
                y = canvasSize.height / 2f + spikeHeight
              ),
              end = Offset(
                x = (canvasSize.width - (spikeWidth + 1)) - (index * (spikeWidth + spikeSpace)),
                y = canvasSize.height / 2f - spikeHeight
              ),
            )

          }
        }
      }
    }
  }

}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  AppRecorderTheme {
    val list = mutableListOf<Float>()
    repeat(700) {
      var item = (1000..28000).random()
      list.add(item.toFloat())
    }
    Box(
      Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.Center,
    ) {
      AmplitudesGraph(
        recorderState = { RecorderState.IDLE },
        amplitudesList = { list },
      )
    }
  }
}