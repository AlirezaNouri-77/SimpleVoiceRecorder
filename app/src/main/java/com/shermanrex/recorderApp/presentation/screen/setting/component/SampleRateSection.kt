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
import com.shermanrex.recorderApp.data.util.convertHzToKhz

@Composable
fun SampleRateSection(
  sampleRateList: List<Int>,
  currentSampleRate: Int,
  onItemClick: (Int) -> Unit,
) {
  Column {
    Text(text = "Sample Rate", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    Text(
      text = "Sample rate in audio is how often the sound is digitally measured per second",
      fontWeight = FontWeight.Light,
      fontSize = 13.sp
    )
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(sampleRateList) { item ->
        FilterChip(
          selected = item == currentSampleRate,
          onClick = { if (item != currentSampleRate) onItemClick(item) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
          ),
          border = BorderStroke(0.dp, Color.Transparent),
          label = {
            Text(text = item.convertHzToKhz(), fontWeight = FontWeight.Medium, fontSize = 14.sp)
          },
        )
      }
    }
  }
}