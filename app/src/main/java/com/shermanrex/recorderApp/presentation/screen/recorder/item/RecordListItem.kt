package com.shermanrex.recorderApp.presentation.screen.recorder.item

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.data.model.RecordModel
import com.shermanrex.recorderApp.data.util.convertByteToReadableSize
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.data.util.convertToKhz
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordListItem(
  modifier: Modifier = Modifier,
  data: RecordModel,
  itemIndex: Int,
  currentItemIndex: Int,
  onItemClick: (Int) -> Unit,
  onLongItemClick: (Int) -> Unit,
) {

  val animateColor =
    if (currentItemIndex == itemIndex) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.Transparent

  Surface(
    modifier = modifier
      .combinedClickable(
        onLongClick = { onLongItemClick(itemIndex) },
        onClick = {
          Log.d("TAG9898", "RecordListItem: " + data.duration)
          onItemClick(itemIndex)
        }
      )
      .fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = animateColor,
  ) {

    Column(
      modifier = Modifier.padding(10.dp)
    ) {
      Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Column(
          modifier = Modifier.weight(0.8f),
          verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = data.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary,
          )
          Text(
            text = "${data.bitrate.convertToKbps()}, ${data.size.convertByteToReadableSize()}, ${data.sampleRate.convertToKhz()}, ${data.format}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onPrimary,
          )
        }
        Text(
          text = data.duration.convertMilliSecondToTime(false),
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,

          color = MaterialTheme.colorScheme.onPrimary,
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
    RecordListItem(
      modifier = Modifier.background(MaterialTheme.colorScheme.background),
      data = RecordModel(
        path = Uri.EMPTY,
        fullName = "Hans Richards",
        name = "Leola Gaines",
        duration = 3607,
        format = "m4a",
        bitrate = 1508,
        sampleRate = 2737,
        size = 3768
      ),
      itemIndex = 1,
      currentItemIndex = 1,
      onItemClick = {},
      onLongItemClick = {}
    )
  }
}