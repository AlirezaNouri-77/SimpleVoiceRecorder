package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaMetadata
import com.shermanrex.recorderApp.domain.model.RecorderState
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState


@Composable
fun BottomSection(
  modifier: Modifier = Modifier,
  isOnSelectMode: Boolean,
  onDismissSelectMode: () -> Unit,
  onSelectAll: () -> Unit,
  onDeleteSelectModeClick: () -> Unit,
  onDeSelectAll: () -> Unit,
  selectedItemCount: () -> Int,
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

  val gradientColor = MaterialTheme.colorScheme.primary

  Box(
    modifier = modifier
      .drawWithCache {
        onDrawBehind {
          this.drawContext
          drawRect(
            brush = Brush.verticalGradient(
              0.4f to Color.Transparent,
              1f to gradientColor,
            )
          )
        }
      },
    contentAlignment = Alignment.Center,
  ) {
    AnimatedContent(
      targetState = currentPlayerState().mediaMetadata != MediaMetadata.EMPTY,
      transitionSpec = {
        fadeIn(tween(50))
          .plus(
            slideIntoContainer(
              towards = AnimatedContentTransitionScope.SlideDirection.Up,
              animationSpec = tween(100, 50)
            )
          )
          .togetherWith(
            slideOutOfContainer(
              towards = AnimatedContentTransitionScope.SlideDirection.Down,
              animationSpec = tween(100, 50)
            )
              .plus(
                fadeOut(tween(50))
              )
          )
      },
      label = "",
      contentAlignment = Alignment.Center,
    ) { targetState ->
      when (targetState) {

        true -> Player(
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

        else -> {
          Card(
            modifier = modifier
              .fillMaxWidth()
              .wrapContentSize()
              .navigationBarsPadding()
              .padding(horizontal = 10.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(
              defaultElevation = 10.dp
            ),
          ) {
            AnimatedContent(isOnSelectMode, label = "") { targetState ->
              when (targetState) {
                true -> SelectedMode(
                    selectedItemCount = { selectedItemCount() },
                    onDismiss = { onDismissSelectMode() },
                    onSelectAll = { onSelectAll() },
                    onDeSelectAll = { onDeSelectAll() },
                    onDeleteClick = { onDeleteSelectModeClick() },
                  )

                false -> Recorder(
                  recorderState = { recorderState() },
                  onPauseRecordClick = { onPauseRecordClick() },
                  onStopRecordClick = { onStopRecordClick() },
                  onStartRecordClick = { onStartRecordClick() },
                  onResumeRecordClick = { onResumeRecordClick() },
                )

              }
            }
          }
        }
      }

    }

  }

}

