package com.shermanrex.recorderApp.presentation.screen.setting.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting

@Composable
fun FormatSection(
  currentItem: RecordAudioSetting,
  onItemClick: (AudioFormat) -> Unit,
) {
  Column {
    Text(text = "Audio Format", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(listOf(AudioFormat.M4A, AudioFormat.THREEGPP, AudioFormat.WAV)) { item ->
        FilterChip(
          selected = item == currentItem.format,
          onClick = { if (item != currentItem.format) onItemClick(item) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
          ),
          border = BorderStroke(0.dp, Color.Transparent),
          label = {
            Text(text = item.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
          },
        )
      }
    }
  }
}
