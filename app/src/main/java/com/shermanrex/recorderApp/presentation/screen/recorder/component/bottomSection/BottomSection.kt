package com.shermanrex.presentation.screen.recorder.component.bottomSection

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaMetadata
import com.shermanrex.recorderApp.data.model.RecorderState
import com.shermanrex.recorderApp.data.model.uiState.CurrentMediaPlayerState

@Composable
fun BottomSection(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  onPauseRecordClick: () -> Unit,
  onStopRecordClick: () -> Unit,
  onStartRecordClick: () -> Unit,
  onResumeRecordClick: () -> Unit,
  currentPlayerState: () -> CurrentMediaPlayerState,
  onResumePlayClick: () -> Unit,
  onPausePlayClick: () -> Unit,
  onSliderValueChange: (Float) -> Unit,
  onFastForwardClick: () -> Unit,
  onFastBackwardClick: () -> Unit,
  onDeleteClick: (Int) -> Unit,
  onShareClick: (itemUri: Uri) -> Unit,
  onClosePlayer: () -> Unit,
  currentPosition: () -> Long,
) {

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    AnimatedContent(
      targetState = currentPlayerState().mediaMetadata != MediaMetadata.EMPTY,
      transitionSpec = {
        fadeIn(tween(150))
          .plus(
            slideIntoContainer(
              towards = AnimatedContentTransitionScope.SlideDirection.Up,
              animationSpec = tween(150, 50)
            )
          )
          .togetherWith(
            fadeOut(tween(100))
              .plus(
                slideOutOfContainer(
                  towards = AnimatedContentTransitionScope.SlideDirection.Down,
                  animationSpec = tween(50, 50)
                )
              )
          )
      },
      label = "",
      contentAlignment = Alignment.Center,
    ) { targetState ->
      when (targetState) {
        true -> {
          Player(
            onResumePlayClick = { onResumePlayClick() },
            onPausePlayClick = { onPausePlayClick() },
            onFastForwardClick = { onFastForwardClick() },
            onFastBackwardClick = { onFastBackwardClick() },
            onDeleteClick = { onDeleteClick(it) },
            onShareClick = { onShareClick(it) },
            onClosePlayer = { onClosePlayer() },
            onSliderValueChange = { onSliderValueChange(it) },
            currentPosition = { currentPosition() },
            currentPlayerState = { currentPlayerState() }
          )
        }

        else -> {
          Recorder(
            recorderState = { recorderState() },
            onPauseRecordClick = { onPauseRecordClick() },
            onStopRecordClick = { onStopRecordClick() },
            onStartRecordClick = { onStartRecordClick() },
            onResumeRecordClick = { onResumeRecordClick() }
          )
        }
      }

    }

  }

}
