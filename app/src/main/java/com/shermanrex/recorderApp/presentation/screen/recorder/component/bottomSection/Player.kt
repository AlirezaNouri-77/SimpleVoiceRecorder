package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.shermanrex.presentation.screen.component.util.NoRipple
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.data.Constant
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(
  modifier: Modifier = Modifier,
  onResumePlayClick: () -> Unit,
  onPausePlayClick: () -> Unit,
  onSliderValueChange: (Float) -> Unit,
  onFastForwardClick: () -> Unit,
  onFastBackwardClick: () -> Unit,
  onDeleteClick: (Int) -> Unit,
  onShareClick: (itemUri: Uri) -> Unit,
  onClosePlayer: () -> Unit,
  currentPosition: () -> Long,
  currentPlayerState: () -> CurrentMediaPlayerState,
  scope: CoroutineScope = rememberCoroutineScope(),
) {

  val sliderTrackScale = remember {
    Animatable(0f)
  }
  var onChangeSlider by remember {
    mutableFloatStateOf(0f)
  }
  var onSeekSlider by remember {
    mutableStateOf(false)
  }
  val sliderValue = if (onSeekSlider) onChangeSlider else currentPosition().toFloat()
  val icon = if (currentPlayerState().isPlaying) R.drawable.pause else R.drawable.icon_play

  LaunchedEffect(onSeekSlider) {
    scope.launch(Dispatchers.Main.immediate) {
      when (onSeekSlider) {
        true -> sliderTrackScale.animateTo(0.3f)
        else -> sliderTrackScale.animateTo(0f)
      }
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentSize()
      .mandatorySystemGesturesPadding()
      .padding(horizontal = 6.dp, vertical = 15.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary,
    ),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(
      defaultElevation = 10.dp
    ),
  ) {

    Column(
      modifier = Modifier.padding(horizontal = 7.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(fraction = 0.95f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          modifier = Modifier.weight(0.9f),
          text = currentPlayerState().mediaMetadata.displayTitle?.toString() ?: "Nothing play",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimary,
        )
        IconButton(
          modifier = Modifier.weight(0.1f, false),
          onClick = { onClosePlayer() },
        ) {
          Icon(
            painter = painterResource(id = R.drawable.icon_arrow_down),
            contentDescription = ""
          )
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
      ) {
        Text(
          modifier = Modifier.weight(0.15f, false),
          text = currentPosition().convertMilliSecondToTime(false),
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onPrimary,
        )
        Box(modifier = Modifier.weight(0.9f, true)) {
          Slider(
            value = 1f,
            enabled = false,
            onValueChange = {},
            track = { sliderState ->
              SliderDefaults.Track(
                modifier = Modifier
                  .scale(1f, 0.7f)
                  .clip(RoundedCornerShape(10.dp)),
                sliderState = sliderState,
                drawStopIndicator = {},
                colors = SliderDefaults.colors(
                  activeTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                  disabledActiveTrackColor = Color.Transparent,
                ),
                trackInsideCornerSize = 0.dp,
                thumbTrackGapSize = 0.dp,
              )
            },
            colors = SliderDefaults.colors(
              disabledThumbColor = Color.Transparent,
              disabledActiveTrackColor = MaterialTheme.colorScheme.onPrimary,
              disabledInactiveTrackColor = MaterialTheme.colorScheme.onPrimary,
              disabledActiveTickColor = Color.Transparent,
            ),
          )
          Slider(
            modifier = Modifier
              .fillMaxWidth(),
            value = sliderValue,
            onValueChangeFinished = {
              onSliderValueChange(onChangeSlider)
              onSeekSlider = false
            },
            onValueChange = { float ->
              onChangeSlider = float
              onSeekSlider = true
            },
            thumb = {
              SliderDefaults.Thumb(
                interactionSource = NoRipple,
                colors = SliderDefaults.colors(
                  thumbColor = Color.Transparent,
                  disabledThumbColor = Color.Transparent,
                ),
                thumbSize = DpSize.Zero
              )
            },
            track = { sliderState ->
              SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier
                  .graphicsLayer {
                    scaleY = 0.8f + sliderTrackScale.value
                  }
                  .clip(RoundedCornerShape(10.dp)),
                colors = SliderDefaults.colors(
                  activeTrackColor = MaterialTheme.colorScheme.onPrimary,
                  inactiveTrackColor = Color.Transparent,
                ),
                drawStopIndicator = {},
                thumbTrackGapSize = 0.dp,
              )
            },
            valueRange = 0f..(currentPlayerState().mediaMetadata.extras?.getInt("duration")?.toFloat() ?: 0f),
          )
        }

        Text(
          modifier = Modifier.weight(0.15f, false),
          text = currentPlayerState().mediaMetadata.extras?.getInt("duration")
            ?.convertMilliSecondToTime(false) ?: 0f.toString(),
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onPrimary,
        )

      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        IconButton(
          modifier = Modifier.weight(0.1f, false),
          onClick = {
            onShareClick(
              currentPlayerState().mediaMetadata.extras?.getString(Constant.METADATA_URI_KEY)
                ?.toUri() ?: Uri.EMPTY
            )
          },
        ) {
          Icon(
            painter = painterResource(id = R.drawable.icon_share),
            contentDescription = "",
            modifier = Modifier.size(
              22.dp
            ),
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.9f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          IconButton(
            onClick = { onFastBackwardClick() },
          ) {
            Icon(
              painter = painterResource(id = R.drawable.icon_fast_backward),
              contentDescription = "",
              modifier = Modifier.size(
                24.dp
              ),
              tint = MaterialTheme.colorScheme.onPrimary,
            )
          }
          IconButton(
            onClick = {
              when (currentPlayerState().isPlaying) {
                true -> onPausePlayClick()
                false -> onResumePlayClick()
              }
            },
          ) {
            Icon(
              painter = painterResource(id = icon),
              contentDescription = "",
              modifier = Modifier.size(
                26.dp
              ),
              tint = MaterialTheme.colorScheme.onPrimary,
            )
          }
          IconButton(
            onClick = { onFastForwardClick() },
          ) {
            Icon(
              painter = painterResource(id = R.drawable.icon_fast_forward),
              contentDescription = "",
              modifier = Modifier.size(
                24.dp
              ),
              tint = MaterialTheme.colorScheme.onPrimary,
            )
          }
        }
        IconButton(
          modifier = Modifier.weight(0.1f, false),
          onClick = {
            onDeleteClick(0)
          },
        ) {
          Icon(
            painter = painterResource(id = R.drawable.icon_delete),
            contentDescription = "",
            modifier = Modifier.size(
              22.dp
            ),
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        }

      }
    }

  }

}

@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  AppRecorderTheme {
    Player(
      onResumePlayClick = {},
      onPausePlayClick = {},
      onSliderValueChange = {},
      onFastForwardClick = {},
      onFastBackwardClick = {},
      onDeleteClick = {},
      onShareClick = {},
      onClosePlayer = {},
      currentPlayerState = { CurrentMediaPlayerState() },
      currentPosition = { 0L },
    )
  }
}