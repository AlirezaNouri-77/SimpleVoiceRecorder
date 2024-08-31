package com.shermanrex.recorderApp.presentation.screen.recorder

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shermanrex.presentation.screen.component.util.getLongPressOffset
import com.shermanrex.presentation.screen.recorder.component.DeleteDialog
import com.shermanrex.presentation.screen.recorder.component.DialogNamePicker
import com.shermanrex.presentation.screen.recorder.component.bottomSection.BottomSection
import com.shermanrex.recorderApp.domain.model.SettingNameFormat
import com.shermanrex.recorderApp.domain.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiState
import com.shermanrex.recorderApp.presentation.screen.recorder.component.ListDropDownMenu
import com.shermanrex.recorderApp.presentation.screen.recorder.component.TopSection
import com.shermanrex.recorderApp.presentation.screen.recorder.item.RecordListItem
import com.shermanrex.recorderApp.presentation.screen.setting.Setting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecorderScreen(
  modifier: Modifier = Modifier,
  viewModel: AppRecorderViewModel = hiltViewModel(),
  density: Density = LocalDensity.current,
  context: Context = LocalContext.current,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val lazyListState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  val recordTimer = viewModel.recordTime.collectAsStateWithLifecycle().value

  var lazyListBottomPadding by remember { mutableStateOf(0.dp) }
  var lazyListTopPadding by remember { mutableStateOf(0.dp) }

  val centerIndicatorVisibility = remember(viewModel.screenRecorderScreenUiState.value) {
    viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.EMPTY ||
         viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.LOADING
  }

  LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
    scope.launch {
      viewModel.serviceConnection.bindService()
    }
  }

  DisposableEffect(key1 = lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_DESTROY) {
        viewModel.serviceConnection.unBindService()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  ListDropDownMenu(
    dropDownMenuState = { viewModel.dropDownMenuState },
    onDismiss = {
      viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = false)
    },
    onRenameClick = {
      viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = false)
      viewModel.showRenameDialog = true
    },
    onDeleteClick = {
      viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = false)
      viewModel.showDeleteDialog = true
    }
  )


  if (viewModel.showDeleteDialog) {
    DeleteDialog(
      item = viewModel.recordDataList[viewModel.dropDownMenuState.itemIndex],
      onDismiss = { viewModel.showDeleteDialog = false },
      onAccept = {
        viewModel.showDeleteDialog = false
        viewModel.deleteRecord(it)
      },
    )
  }

  if (viewModel.showSettingBottomSheet) {
    Setting(
      sheetSate = sheetState,
      recorderState = viewModel.recorderState,
      onDismiss = { viewModel.showSettingBottomSheet = false },
      currentAudioFormat = viewModel.currentAudioFormat,
      onAudioBitRateClick = {
        viewModel.currentAudioFormat = viewModel.currentAudioFormat.copy(bitrate = it)
        viewModel.writeAudioBitrate(it)
      },
      onAudioFormatClick = {
        viewModel.currentAudioFormat = viewModel.currentAudioFormat.copy(
          format = it,
          bitrate = it.defaultBitRate,
          sampleRate = it.defaultSampleRate
        )
        viewModel.writeAudioFormat(it)
      },
      onNewSavePath = {
        viewModel.writeDataStoreSavePath(it.toString())
        viewModel.getRecords()
      },
      onAudioSampleRateClick = {
        viewModel.currentAudioFormat = viewModel.currentAudioFormat.copy(sampleRate = it)
        viewModel.writeSampleRate(it)
      },
      onNameFormatClick = { viewModel.writeDataStoreNameFormat(it) },
      getCurrentFileName = { viewModel.getDataStoreNameFormat() },
      getCurrentSavePath = { viewModel.getDataStoreSavePath().toUri() }
    )
  }

  if (viewModel.showRenameDialog) {
    val currentItem = viewModel.recordDataList[viewModel.dropDownMenuState.itemIndex]
    DialogNamePicker(
      title = "Rename",
      label = "",
      defaultText = currentItem.name,
      positiveText = "Rename",
      negativeText = "Cancel",
      onDismiss = { viewModel.showRenameDialog = false },
      onPositive = {
        viewModel.renameRecord(targetItem = currentItem, newName = it)
        viewModel.showRenameDialog = false
      },
    )
  }

  if (viewModel.savePathNotFound) {
    AlertDialog(
      title = { Text(text = "Save Path not found") },
      text = { Text(text = "cant start record because save path not found please go in app setting and choose save path") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.savePathNotFound = false
            viewModel.showSettingBottomSheet = true
          },
        ) {
          Text(text = "open setting")
        }
      },
      onDismissRequest = {
        viewModel.savePathNotFound = false
      },
    )
  }

  if (viewModel.showNamePickerDialog) {
    DialogNamePicker(
      title = "Record",
      label = "Enter a name",
      defaultText = "",
      positiveText = "Start",
      negativeText = "Cancel",
      onDismiss = { viewModel.showNamePickerDialog = false },
      onPositive = {
        viewModel.sendActionToService(ServiceActionNotification.START)
        viewModel.startRecord(it)
      },
    )
  }

  LaunchedEffect(viewModel.showSettingBottomSheet) {
    if (viewModel.showSettingBottomSheet) {
      scope.launch(Dispatchers.Main) {
        sheetState.show()
      }
    } else {
      scope.launch(Dispatchers.Main) {
        sheetState.hide()
      }
    }
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .getLongPressOffset {
        viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(longPressOffset = it)
      },
    color = MaterialTheme.colorScheme.background,
  ) {

    ConstraintLayout(
      modifier = Modifier
        .fillMaxSize(),
    ) {

      val (topSection, recordsControlRef, recordLazyList, centerIndicatorText) = createRefs()

      Crossfade(
        modifier = Modifier
          .fillMaxSize()
          .constrainAs(recordLazyList) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
          },
        targetState = viewModel.screenRecorderScreenUiState.value,
        label = "",
      ) { state ->

        LazyColumn(
          state = lazyListState,
          horizontalAlignment = Alignment.CenterHorizontally,
          contentPadding = PaddingValues(
            bottom = lazyListBottomPadding,
            top = lazyListTopPadding,
            start = 8.dp,
            end = 8.dp,
          ),
        ) {
          when (state) {
            RecorderScreenUiState.DATA -> {
              itemsIndexed(
                items = viewModel.recordDataList,
                key = { _, item -> item.id },
              ) { index, item ->
                RecordListItem(
                  modifier = Modifier.animateItemPlacement(),
                  itemIndex = index,
                  currentItemIndex = viewModel.currentIndexClick,
                  data = item,
                  onItemClick = {
                    viewModel.startPlayAudio(item)
                    viewModel.currentIndexClick = it
                  },
                  onLongItemClick = {
                    viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = true, itemIndex = it)
                  },
                )
              }
            }

            else -> {}
          }

        }
      }


      AnimatedVisibility(
        visible = centerIndicatorVisibility,
        modifier = Modifier
          .constrainAs(centerIndicatorText) {
            top.linkTo(topSection.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(recordsControlRef.bottom)
          },
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Box(
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = if (viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.EMPTY) "No Record" else "Loading",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
          )
        }
      }


      TopSection(
        modifier = Modifier
          .constrainAs(topSection) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          }
          .onGloballyPositioned {
            with(density) {
              lazyListTopPadding = it.size.height.toDp() + 15.dp
            }
          },
        amplitudesList = { viewModel.amplitudesList },
        recordTime = { recordTimer },
        recorderState = viewModel.recorderState,
        currentAudioSetting = viewModel.currentAudioFormat,
        onSettingClick = { viewModel.showSettingBottomSheet = true },
      )

      BottomSection(
        modifier = Modifier
          .constrainAs(recordsControlRef) {
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
          }
          .onGloballyPositioned {
            with(density) {
              lazyListBottomPadding = it.size.height.toDp()
            }
          },
        recorderState = { viewModel.recorderState },
        onStartRecordClick = {
          scope.launch {
            val name = viewModel.getDataStoreNameFormat()
            if (name != SettingNameFormat.ASK_ON_RECORD) {
              viewModel.sendActionToService(ServiceActionNotification.START)
              viewModel.startRecord("")
            } else {
              viewModel.showNamePickerDialog = true
            }
          }
        },
        onDeleteClick = {
          viewModel.showDeleteDialog = true
          viewModel.currentIndexClick = -1
        },
        onShareClick = { itemUri ->
          val intent = Intent().apply {
            setAction(Intent.ACTION_SEND)
            putExtra(Intent.EXTRA_STREAM, itemUri)
            setType("audio/*")
          }
          context.startActivity(intent)
        },
        currentPosition = { viewModel.currentPlayerPosition.toLong() },
        onClosePlayer = {
          viewModel.stopPlayAudio()
          viewModel.currentIndexClick = -1
        },
        onResumeRecordClick = viewModel::resumeRecord,
        onStopRecordClick = viewModel::stopRecord,
        onPauseRecordClick = viewModel::pauseRecord,
        currentPlayerState = viewModel::mediaPlayerState,
        onSliderValueChange = viewModel::seekToPosition,
        onPausePlayClick = viewModel::pauseAudio,
        onResumePlayClick = viewModel::resumeAudio,
        onFastBackwardClick = viewModel::fastBackForwardAudio,
        onFastForwardClick = viewModel::fastForwardAudio,
      )

    }

  }
}

