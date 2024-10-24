package com.shermanrex.recorderApp.presentation.screen.setting.component

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.domain.model.record.RecorderState

@Composable
fun SavePathSection(
  recorderState: RecorderState,
  currentSavePath: Uri,
  onPathClick: () -> Unit,
) {

  Column {
    Text(text = "Save Path", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    TextButton(
      enabled = recorderState == RecorderState.IDLE,
      modifier = Modifier.fillMaxWidth(),
      onClick = { onPathClick() },
      colors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        disabledContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
      ),
    ) {
      Text(text = currentSavePath.path.toString())
    }
  }
}