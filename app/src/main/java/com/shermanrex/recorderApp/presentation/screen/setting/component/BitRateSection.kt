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
import com.shermanrex.recorderApp.data.util.convertToKbps

@Composable
fun BitRateSection(
  bitrate: List<Int>,
  currentBitRate: Int,
  onItemClick: (Int) -> Unit,
) {
  Column {
    Text(text = "BitRate", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    Text(
      text = "Bitrate in audio is the amount of data per second, affecting sound quality",
      fontWeight = FontWeight.Light,
      fontSize = 13.sp
    )
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(bitrate) { item ->
        FilterChip(
          selected = item == currentBitRate,
          onClick = { if (item != currentBitRate) onItemClick(item) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
          ),
          border = BorderStroke(0.dp, Color.Transparent),
          label = {
            Text(text = item.convertToKbps(), fontWeight = FontWeight.Medium, fontSize = 14.sp)
          },
        )
      }
    }
  }
}