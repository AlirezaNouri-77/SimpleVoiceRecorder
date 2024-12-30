package com.shermanrex.recorderApp.presentation.screen.setting

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.shermanrex.presentation.screen.setting.component.NameSection
import com.shermanrex.recorderApp.domain.model.record.AudioFormat
import com.shermanrex.recorderApp.domain.model.record.RecordAudioSetting
import com.shermanrex.recorderApp.domain.model.record.RecorderState
import com.shermanrex.recorderApp.domain.model.record.SettingNameFormat
import com.shermanrex.recorderApp.presentation.screen.setting.component.BitRateSection
import com.shermanrex.recorderApp.presentation.screen.setting.component.FormatSection
import com.shermanrex.recorderApp.presentation.screen.setting.component.SampleRateSection
import com.shermanrex.recorderApp.presentation.screen.setting.component.SavePathSection
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  sheetSate: SheetState,
  recorderState: RecorderState,
  currentAudioFormat: RecordAudioSetting,
  onPathClick: () -> Unit,
  onNameFormatClick: (SettingNameFormat) -> Unit,
  onAudioFormatClick: (AudioFormat) -> Unit,
  onAudioBitRateClick: (Int) -> Unit,
  onAudioSampleRateClick: (Int) -> Unit,
  getCurrentFileName: suspend () -> SettingNameFormat,
  getCurrentSavePath: suspend () -> Uri,
) {

  var currentFileName by remember {
    mutableStateOf(SettingNameFormat.TIME)
  }
  var currentSavePath by remember {
    mutableStateOf(Uri.EMPTY)
  }

  LaunchedEffect(sheetSate) {
    currentFileName = getCurrentFileName()
    currentSavePath = getCurrentSavePath()
  }

  ModalBottomSheet(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    onDismissRequest = { onDismiss() },
    sheetState = sheetSate,
    tonalElevation = 15.dp,
    shape = RoundedCornerShape(topEnd = 25.dp, topStart = 25.dp),
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onPrimary,
  ) {
    BottomSheetContent(
      currentFileName = currentFileName,
      currentSavePath = currentSavePath,
      currentAudioFormat = currentAudioFormat,
      onNameFormatClick = {
        currentFileName = it
        onNameFormatClick(it)
      },
      onAudioFormatClick = { onAudioFormatClick(it) },
      onAudioBitRateClick = { onAudioBitRateClick(it) },
      onAudioSampleRateClick = { onAudioSampleRateClick(it) },
      onPathClick = { onPathClick() },
      recorderState = recorderState,
    )
  }

}

@Composable
fun BottomSheetContent(
  modifier: Modifier = Modifier,
  currentFileName: SettingNameFormat,
  recorderState: RecorderState,
  currentSavePath: Uri,
  currentAudioFormat: RecordAudioSetting,
  onNameFormatClick: (SettingNameFormat) -> Unit,
  onAudioFormatClick: (AudioFormat) -> Unit,
  onAudioBitRateClick: (Int) -> Unit,
  onAudioSampleRateClick: (Int) -> Unit,
  onPathClick: () -> Unit,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(start = 15.dp, end = 15.dp, bottom = 5.dp),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = "Setting",
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp
      )
      NameSection(
        currentItem = currentFileName,
        onItemClick = { item, _ ->
          onNameFormatClick(item)
        },
      )
      FormatSection(
        currentItem = currentAudioFormat,
        onItemClick = {
          onAudioFormatClick(it)
        },
      )
      BitRateSection(
        bitrate = currentAudioFormat.format.bitRate,
        currentBitRate = currentAudioFormat.bitrate,
        onItemClick = { onAudioBitRateClick(it) },
      )
      SampleRateSection(
        sampleRateList = currentAudioFormat.format.sampleRate,
        currentSampleRate = currentAudioFormat.sampleRate,
        onItemClick = { onAudioSampleRateClick(it) },
      )
      SavePathSection(
        recorderState = recorderState,
        currentSavePath = currentSavePath,
        onPathClick = { onPathClick() },
      )
    }
  }
}

@Preview(showBackground = true)
@Preview(
  showBackground = true,
  uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
  AppRecorderTheme {
    BottomSheetContent(
      modifier = Modifier.background(MaterialTheme.colorScheme.background),
      currentAudioFormat = RecordAudioSetting(
        AudioFormat.M4A,
        bitrate = 128_000,
        sampleRate = 441_000
      ),
      currentFileName = SettingNameFormat.TIME,
      currentSavePath = "Test Path".toUri(),
      onNameFormatClick = { SettingNameFormat.ASK_ON_RECORD },
      onAudioFormatClick = { AudioFormat.M4A },
      onAudioBitRateClick = {},
      onAudioSampleRateClick = {},
      onPathClick = {},
      recorderState = RecorderState.IDLE,
    )
  }
}