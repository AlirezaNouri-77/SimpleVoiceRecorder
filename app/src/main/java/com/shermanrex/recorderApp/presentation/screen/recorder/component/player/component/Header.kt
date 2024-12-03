package com.shermanrex.recorderApp.presentation.screen.recorder.component.player.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.R

@Composable
fun Header(
  modifier: Modifier = Modifier,
  name: String,
  onClosePlayer: () -> Unit,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      modifier = Modifier.weight(0.9f).basicMarquee(),
      text = name,
      fontSize = 18.sp,
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
}