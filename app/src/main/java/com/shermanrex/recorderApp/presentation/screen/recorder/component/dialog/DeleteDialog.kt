package com.shermanrex.recorderApp.presentation.screen.recorder.component.dialog

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shermanrex.recorderApp.domain.model.RecordModel
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun DeleteDialog(
  modifier: Modifier = Modifier,
  item: RecordModel,
  onDismiss: () -> Unit,
  onAccept: (RecordModel) -> Unit,
) {

  Dialog(
    onDismissRequest = { onDismiss() },
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth()
        .padding(10.dp),
      shape = RoundedCornerShape(15.dp),
      colors = CardDefaults.cardColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(text = "Attention", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = "Are you sure about delete this audio file?", fontSize = 15.sp)
        Text(text = "${item.name} will be delete", fontSize = 14.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
          Button(
            onClick = { onDismiss() },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.Transparent,
            ),
          ) {
            Text(text = "No")
          }
          Button(
            onClick = { onAccept(item) },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
          ) {
            Text(text = "Yes")
          }
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
    DeleteDialog(
      item = RecordModel(
        path = Uri.EMPTY,
        fullName = "",
        name = "Record 2",
        duration = 3190,
        format = ".m4a",
        bitrate = 7369,
        sampleRate = 8948,
        size = 4111,
        date = "",
      ),
      onDismiss = { /*TODO*/ },
      onAccept = {},
    )
  }
}