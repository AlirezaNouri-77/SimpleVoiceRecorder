package com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.data.Constant
import com.shermanrex.recorderApp.domain.model.uiState.CurrentMediaPlayerState

@Composable
fun Controller(
  modifier: Modifier = Modifier,
  onResumePlayClick: () -> Unit,
  onPausePlayClick: () -> Unit,
  onFastForwardClick: () -> Unit,
  onFastBackwardClick: () -> Unit,
  onDeleteClick: (Int) -> Unit,
  onShareClick: (itemUri: Uri) -> Unit,
  currentPlayerState: () -> CurrentMediaPlayerState,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    IconButton(
      modifier = Modifier.weight(0.1f, false),
      onClick = {
        val itemUri = currentPlayerState().mediaMetadata.extras?.getString(Constant.METADATA_URI_KEY)
          ?.toUri() ?: Uri.EMPTY
        onShareClick(itemUri)
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
          painter = painterResource(id = if (currentPlayerState().isPlaying || currentPlayerState().isBuffering) R.drawable.pause else R.drawable.icon_play),
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