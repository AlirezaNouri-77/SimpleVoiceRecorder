package com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSlider(
  modifier: Modifier = Modifier,
  duration: Float,
  onSliderValueChange: (Float) -> Unit,
  currentPosition: () -> Long,
) {

  var coroutineScope = rememberCoroutineScope()

  var onSliderSeeking by remember { mutableStateOf(false) }

  val sliderTrackScale = remember {
    Animatable(0f)
  }

  var onChangeSlider by remember {
    mutableFloatStateOf(0f)
  }

  var onSeekSlider by remember {
    mutableStateOf(false)
  }

  val sliderThumbWidth = animateDpAsState(targetValue = if (onSliderSeeking) 7.dp else 5.dp, label = "").value

  val sliderTrackHeight = animateDpAsState(targetValue = if (onSliderSeeking) 4.dp else 8.dp, label = "").value

  var interactionSource = remember { MutableInteractionSource() }

  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect {
      when (it) {
        is DragInteraction.Start -> onSliderSeeking = true
        is DragInteraction.Stop, is DragInteraction.Cancel -> onSliderSeeking = false
      }
    }
  }

  LaunchedEffect(onSeekSlider) {
    coroutineScope.launch(Dispatchers.Main.immediate) {
      when (onSeekSlider) {
        true -> sliderTrackScale.animateTo(0.3f)
        else -> sliderTrackScale.animateTo(0f)
      }
    }
  }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceAround,
  ) {
    Text(
      modifier = Modifier.weight(0.15f, false),
      text = (if (onSliderSeeking) onChangeSlider else currentPosition()).convertMilliSecondToTime(false),
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onPrimary,
    )
    Slider(
      value = if (onSliderSeeking) onChangeSlider else currentPosition().toFloat(),
      modifier = Modifier
        .weight(0.9f, true),
      onValueChangeFinished = {
        onSliderValueChange(onChangeSlider)
      },
      onValueChange = { float ->
        onChangeSlider = float
      },
      interactionSource = interactionSource,
      thumb = {
        SliderDefaults.Thumb(
          interactionSource = interactionSource,
          thumbSize = DpSize(width = sliderThumbWidth, height = 18.dp),
          colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.onPrimary,
          ),
        )
      },
      track = { sliderState ->
        SliderDefaults.Track(
          modifier = Modifier
            .height(sliderTrackHeight)
            .clip(RoundedCornerShape(5.dp)),
          sliderState = sliderState,
          colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            inactiveTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
          ),
          drawStopIndicator = null,
          thumbTrackGapSize = 3.dp,
          trackInsideCornerSize = 3.dp,
        )
      },
      valueRange = 0f..duration,
    )

    Text(
      modifier = Modifier.weight(0.15f, false),
      text = duration.convertMilliSecondToTime(false),
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onPrimary,
    )

  }

}