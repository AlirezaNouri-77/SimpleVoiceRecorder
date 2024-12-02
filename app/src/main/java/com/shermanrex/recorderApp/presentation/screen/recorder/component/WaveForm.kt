package com.shermanrex.recorderApp.presentation.screen.recorder.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WaveForm(
  modifier: Modifier = Modifier,
  enable: Boolean = true,
  line: Int = 3,
  lineColor: Color = Color.White,
  density: Density = LocalDensity.current,
) {

  val lineSpace = 5f
  val lineWidth = 10f

  val widthDp = with(density) {
    ((lineSpace + lineWidth) * line).toDp()
  }
  val heightDp = widthDp + 10.dp

  val heightPx = with(density) {
    heightDp.toPx()
  }

  val lineCount = line
  val halfHeight = heightPx / 2

  val animatable = remember {
    Array(lineCount) { Animatable(initialValue = 0f) }
  }

  if (enable) {
    LaunchedEffect(Unit) {
      (0 until lineCount).forEachIndexed { index, _ ->
        val targetValueStartAndEnd = halfHeight - 10f
        launch(Dispatchers.Main.immediate) {
          delay(45L * (5..10).random())
          animatable[index].animateTo(
            targetValue = targetValueStartAndEnd,
            infiniteRepeatable(
              animation = tween(
                durationMillis = (400..600).random(),
                easing = LinearEasing,
              ),
              repeatMode = RepeatMode.Reverse,
            ),
          )
        }
      }
    }
  } else {
    LaunchedEffect(Unit) {
      (0 until lineCount).forEachIndexed { index, _ ->
        launch(Dispatchers.Main.immediate) {
          animatable[index].stop()
          animatable[index].animateTo(
            targetValue = 0f,
            animationSpec = tween(130,60)
          )
        }
      }
    }
  }

  Canvas(
    modifier = modifier
      .size(width = widthDp, height = heightDp)
  ) {
    for (index in 0 until lineCount) {
      inset(horizontal = 2f){
        drawLine(
          color = lineColor,
          start = Offset(x = (index * (lineSpace + lineWidth)) + lineSpace, y = halfHeight + animatable[index].value),
          end = Offset(x = (index * (lineSpace + lineWidth)) + lineSpace, y = halfHeight - animatable[index].value),
          strokeWidth = lineWidth,
          cap = StrokeCap.Round,
        )
      }
    }
  }

}


@Preview(showBackground = true)
@Composable
private fun WavePreview() {
  AppRecorderTheme {
    WaveForm()
  }
}