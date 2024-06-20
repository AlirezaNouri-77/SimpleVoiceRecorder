package com.shermanrex.recorderApp.presentation.screen.setting.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.model.RecorderState

@Composable
fun SavePathSection(
  recorderState: RecorderState,
  savePath: () -> Uri,
  onSavePath: (Uri) -> Unit,
  context: Context = LocalContext.current,
) {

  val safActivityResult =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
      val takeFlags =
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
      it?.let { uri ->
        if (uri != savePath()) {
          context.contentResolver.takePersistableUriPermission(uri, takeFlags)
          onSavePath(uri)
        }
      }
    }

  Column {
    Text(text = "Save Path", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    TextButton(
      enabled = recorderState == RecorderState.IDLE,
      modifier = Modifier.fillMaxWidth(),
      onClick = {
        safActivityResult.launch(savePath())
      },
      colors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        disabledContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
      ),
    ) {
      Text(text = savePath().path.toString())
    }
  }
}