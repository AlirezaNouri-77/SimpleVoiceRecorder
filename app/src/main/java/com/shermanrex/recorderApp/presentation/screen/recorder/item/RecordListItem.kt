package com.shermanrex.recorderApp.presentation.screen.recorder.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.util.convertByteToReadableSize
import com.shermanrex.recorderApp.data.util.convertHzToKhz
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.presentation.screen.recorder.component.WaveForm
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordListItem(
  modifier: Modifier = Modifier,
  data: RecordModel,
  itemIndex: Int,
  currentItemIndex: Int,
  isPlaying: Boolean,
  onSelectMode: Boolean,
  isItemSelected: Boolean,
  onItemClick: (Int) -> Unit,
  onLongItemClick: (Int) -> Unit,
  onCheckBoxClick: (RecordModel) -> Unit,
) {

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(15.dp))
      .combinedClickable(
        onLongClick = {
          onLongItemClick(itemIndex)
        },
        onClick = {
          onItemClick(itemIndex)
        }
      ),
    color = if (currentItemIndex == itemIndex) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f) else Color.Transparent,
  ) {

    Row(
      modifier = Modifier.padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      AnimatedVisibility(
        visible = currentItemIndex == itemIndex,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        WaveForm(
          enable = isPlaying,
          lineColor = MaterialTheme.colorScheme.onPrimary,
        )
      }
      Spacer(Modifier.width(8.dp))
      Column(
        modifier = Modifier.weight(0.8f),
        verticalArrangement = Arrangement.spacedBy(1.dp)
      ) {
        Text(
          text = data.name,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimary,
          maxLines = 2,
        )
        Text(
          text = "${data.bitrate.convertToKbps()}, ${data.size.convertByteToReadableSize()}, ${data.sampleRate.convertHzToKhz()}, ${data.format}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Light,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      }
      Text(
        modifier = Modifier.weight(0.2f),
        text = data.duration.convertMilliSecondToTime(false),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      AnimatedVisibility(
        visible = onSelectMode,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Checkbox(
          checked = isItemSelected,
          onCheckedChange = {
            onCheckBoxClick(data)
          },
          colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
            checkmarkColor = MaterialTheme.colorScheme.onPrimary,
            uncheckedColor = MaterialTheme.colorScheme.onPrimary,
          ),
        )
      }
    }

  }
}

@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
  AppRecorderTheme {
    Column {
      RecordListItem(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        data = RecordModel.Dummy,
        itemIndex = 0,
        currentItemIndex = 1,
        onItemClick = {},
        onLongItemClick = {},
        onCheckBoxClick = {},
        onSelectMode = true,
        isItemSelected = false,
        isPlaying = false,
      )
      RecordListItem(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        data = RecordModel.Dummy,
        itemIndex = 0,
        currentItemIndex = 1,
        onItemClick = {},
        onLongItemClick = {},
        onCheckBoxClick = {},
        onSelectMode = true,
        isItemSelected = true,
        isPlaying = false,
      )
      RecordListItem(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        data = RecordModel.Dummy,
        itemIndex = 1,
        currentItemIndex = 1,
        onItemClick = {},
        onLongItemClick = {},
        onCheckBoxClick = {},
        onSelectMode = false,
        isItemSelected = false,
        isPlaying = true,
      )
      RecordListItem(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        data = RecordModel.Dummy,
        itemIndex = 0,
        currentItemIndex = 1,
        onItemClick = {},
        onLongItemClick = {},
        onCheckBoxClick = {},
        onSelectMode = false,
        isItemSelected = false,
        isPlaying = false,
      )
    }
  }
}