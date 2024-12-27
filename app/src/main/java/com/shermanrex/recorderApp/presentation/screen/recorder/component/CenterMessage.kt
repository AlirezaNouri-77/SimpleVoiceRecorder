package com.shermanrex.recorderApp.presentation.screen.recorder.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CenterMessage(
  modifier: Modifier = Modifier,
  message: String,
  fontSize: TextUnit = 20.sp,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = message,
      fontWeight = FontWeight.Bold,
      fontSize = fontSize,
      color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
    )
  }
}