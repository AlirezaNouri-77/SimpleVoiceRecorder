package com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun SelectedMode(
  modifier: Modifier = Modifier,
  selectedItemCount: () -> Int,
  onDeleteClick: () -> Unit,
  onDeSelectAll: () -> Unit,
  onSelectAll: () -> Unit,
  onDismiss: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxWidth().padding(vertical = 5.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "${selectedItemCount()} Selected",
      fontSize = 18.sp,
      color = MaterialTheme.colorScheme.onPrimary,
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
    ) {
      IconButton(
        modifier = Modifier.weight(0.1f, false),
        onClick = {
          onDismiss()
        },
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_close),
          contentDescription = "",
          modifier = Modifier.size(
            18.dp
          ),
          tint = MaterialTheme.colorScheme.onPrimary,
        )
      }
      IconButton(
        modifier = Modifier.weight(0.1f, false),
        onClick = {
          onDeSelectAll()
        },
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_select_all_off),
          contentDescription = "",
          modifier = Modifier.size(
            22.dp
          ),
          tint = MaterialTheme.colorScheme.onPrimary,
        )
      }
      IconButton(
        modifier = Modifier.weight(0.1f, false),
        onClick = {
          onSelectAll()
        },
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_select_all_on),
          contentDescription = "",
          modifier = Modifier.size(
            22.dp
          ),
          tint = MaterialTheme.colorScheme.onPrimary,
        )
      }
      IconButton(
        modifier = Modifier.weight(0.1f, false),
        onClick = {
          onDeleteClick()
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


@Preview(showBackground = true)
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SelectedModePreview() {
  AppRecorderTheme {
    SelectedMode(
      selectedItemCount = { 10 },
      onDismiss = {},
      onSelectAll = {},
      onDeSelectAll = {},
      onDeleteClick = {},
    )
  }
}