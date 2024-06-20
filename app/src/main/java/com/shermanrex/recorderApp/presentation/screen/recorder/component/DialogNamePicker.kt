package com.shermanrex.presentation.screen.recorder.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun DialogNamePicker(
  modifier: Modifier = Modifier,
  title: String,
  label: String,
  defaultText: String,
  positiveText: String,
  negativeText: String,
  onDismiss: () -> Unit,
  onPositive: (String) -> Unit,
) {

  var textField by remember {
    mutableStateOf(defaultText)
  }
  var textFieldError by remember {
    mutableStateOf("")
  }

  Dialog(
    onDismissRequest = { onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Card(
      modifier = modifier.fillMaxWidth(0.9f),
      shape = RoundedCornerShape(10.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.background,
      ),
    ) {
      Column(
        modifier = Modifier.padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = title,
          modifier = Modifier,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.SemiBold,
          fontSize = 18.sp,
        )
        TextField(
          modifier = Modifier.fillMaxWidth(),
          value = textField,
          onValueChange = { textField = it },
          placeholder = { Text(text = label) },
          supportingText = {
            Text(
              text = textFieldError,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Color.Red,
            )
          },
          shape = RoundedCornerShape(12.dp),
          colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
          ),
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround,
        ) {
          Spacer(modifier = Modifier.width(10.dp))
          Button(
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.Transparent,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            onClick = {
              onDismiss()
            },
          ) {
            Text(text = negativeText)
          }
          Button(
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            onClick = {
              if (textField.isEmpty()) {
                textFieldError = "Text field is empty"
              } else if (textField.length <= 2) {
                textFieldError = "Is to short"
              } else {
                textFieldError = ""
                onPositive(textField.trim())
                onDismiss()
              }
            },
          ) {
            Text(text = positiveText)
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
    DialogNamePicker(
      title = "Record",
      defaultText = "",
      label = "Enter a name",
      positiveText = "Start",
      negativeText = "Cancel",
      onDismiss = {},
      onPositive = {},
    )
  }
}