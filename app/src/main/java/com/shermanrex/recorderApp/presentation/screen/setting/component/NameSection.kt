package com.shermanrex.presentation.screen.setting.component

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
import com.shermanrex.recorderApp.data.util.convertTimeStampToDate
import com.shermanrex.recorderApp.domain.model.SettingNameFormat

@Composable
fun NameSection(
  currentItem: SettingNameFormat,
  onItemClick: (SettingNameFormat, Int) -> Unit,
) {

  val example = if (currentItem != SettingNameFormat.ASK_ON_RECORD) {
    "Example: ${convertTimeStampToDate(currentItem.pattern)}"
  } else "Example: My custom name"
  val list = SettingNameFormat.entries.toList()

  Column {
    Text(text = "File name", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Text(text = example, fontWeight = FontWeight.Light, fontSize = 14.sp)
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(list) { item ->
        FilterChip(
          selected = item == currentItem,
          onClick = { onItemClick(item, item.id) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
          ),
          border = BorderStroke(0.dp, Color.Transparent),
          label = {
            Text(text = item.value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
          },
        )
      }
    }
  }

}