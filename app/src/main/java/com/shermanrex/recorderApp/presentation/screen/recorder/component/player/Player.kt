package com.shermanrex.recorderApp.presentation.screen.recorder.component.player

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState
import com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component.Controller
import com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component.Header
import com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component.RecordSlider
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

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
) {

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

      Header(
        modifier = Modifier.fillMaxWidth(fraction = 0.95f),
        name = currentPlayerState().mediaMetadata.displayTitle?.toString() ?: "Nothing play",
        onClosePlayer = { onClosePlayer() }
      )

      RecordSlider(
        modifier = Modifier.fillMaxWidth(),
        duration = currentPlayerState().mediaMetadata.extras?.getInt("duration")?.toFloat() ?: 0f,
        onSliderValueChange = { onSliderValueChange(it) },
        currentPosition = { currentPosition() }
      )

      Controller(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        onResumePlayClick = { onResumePlayClick() },
        onPausePlayClick = { onPausePlayClick() },
        onFastForwardClick = { onFastForwardClick() },
        onFastBackwardClick = { onFastBackwardClick() },
        onDeleteClick = { onDeleteClick(it) },
        onShareClick = { onShareClick(it) },
        currentPlayerState = { currentPlayerState() },
      )

    }

  }

}


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
      currentPlayerState = { CurrentMediaPlayerState.Empty },
      currentPosition = { 0L },
    )
  }
}