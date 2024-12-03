package com.shermanrex.recorderApp.presentation.screen.recorder.component.topsection.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import kotlin.math.roundToInt

@Composable
fun TopSectionAmplitudesGraph(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  amplitudesList: () -> List<Float>,
) {

  val canvasColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)

  var reCanvas by remember {
    mutableIntStateOf(0)
  }
  LaunchedEffect(amplitudesList().size) {
    ++reCanvas
  }

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    Canvas(
      modifier = Modifier
        .height(100.dp)
        .background(
          MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
          shape = RoundedCornerShape(10.dp)
        )
        .fillMaxWidth()
        .padding(5.dp),
    ) {

      val canvasSize = this.size
      val spikeWidth = 6f
      val spikeSpace = 4f

      val spikeCount = ((canvasSize.width) / (spikeWidth + spikeSpace)).roundToInt()
      val list = amplitudesList().takeLast(spikeCount).asReversed()

      // draw vertical center line
      drawLine(
        color = canvasColor,
        strokeWidth = 2f,
        alpha = 0.4f,
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

            val spikeHeight = item.div(60).coerceAtMost((canvasSize.height / 2) - 20)

            drawLine(
              color = canvasColor,
              strokeWidth = spikeWidth,
              cap = StrokeCap.Round,
              start = Offset(
                x = (canvasSize.width - (spikeWidth / 2)) - (index * (spikeWidth + spikeSpace)),
                y = canvasSize.height / 2f + spikeHeight
              ),
              end = Offset(
                x = (canvasSize.width - (spikeWidth / 2)) - (index * (spikeWidth + spikeSpace)),
                y = canvasSize.height / 2f - spikeHeight
              ),
            )

          }
        }
      }

    }
  }
}