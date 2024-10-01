package com.shermanrex.recorderApp.presentation.screen.recorder

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shermanrex.recorderApp.domain.model.SettingNameFormat
import com.shermanrex.recorderApp.domain.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiEvent
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiState
import com.shermanrex.recorderApp.presentation.screen.component.util.getLongPressOffset
import com.shermanrex.recorderApp.presentation.screen.component.util.shareItem
import com.shermanrex.recorderApp.presentation.screen.recorder.component.ListDropDownMenu
import com.shermanrex.recorderApp.presentation.screen.recorder.component.TopSection
import com.shermanrex.recorderApp.presentation.screen.recorder.component.bottomSection.BottomSection
import com.shermanrex.recorderApp.presentation.screen.recorder.component.dialog.DialogHandler
import com.shermanrex.recorderApp.presentation.screen.recorder.item.RecordListItem
import com.shermanrex.recorderApp.presentation.screen.setting.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorderScreen(
  modifier: Modifier = Modifier,
  viewModel: AppRecorderViewModel = hiltViewModel(),
  density: Density = LocalDensity.current,
  context: Context = LocalContext.current,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  scope: CoroutineScope = rememberCoroutineScope(),
) {

  val uiEvent = viewModel.uiEvent.collectAsStateWithLifecycle(RecorderScreenUiEvent.INITIAL).value
  val recordTimer = viewModel.recordTime.collectAsStateWithLifecycle().value

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var bottomSectionSize by remember { mutableStateOf(0.dp) }
  var topSectionSize by remember { mutableStateOf(0.dp) }

  val safActivityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    it?.let { uri ->
      context.contentResolver.takePersistableUriPermission(uri, takeFlags)
      viewModel.writeDataStoreSavePath(it.toString(), true)
      viewModel.setUiEvent(RecorderScreenUiEvent.INITIAL)
    }
  }

  DisposableEffect(key1 = lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_START -> scope.launch { viewModel.serviceConnection.bindService() }
        Lifecycle.Event.ON_DESTROY -> viewModel.serviceConnection.unBindService()
        else -> {}
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  ListDropDownMenu(
    dropDownMenuState = { viewModel.dropDownMenuState },
    isSelectedMode = viewModel.showSelectMode,
    onDismiss = {
      viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = false)
    },
    onRenameClick = {
      viewModel.setUiEvent(RecorderScreenUiEvent.RENAME_DIALOG)
    },
    onDeleteClick = {
      viewModel.setUiEvent(RecorderScreenUiEvent.DELETE_DIALOG)
    },
    onSelectMode = {
      viewModel.showSelectMode = !viewModel.showSelectMode
    }
  )

  DialogHandler(
    uiEvent = uiEvent,
    currentItem = { viewModel.recordDataList[viewModel.dropDownMenuState.itemIndex] },
    onDismiss = { viewModel.setUiEvent(RecorderScreenUiEvent.INITIAL) },
    onDeleteRecord = {
      viewModel.deleteRecord(it)
    },
    onRenameRecord = { item, name ->
      viewModel.renameRecord(targetItem = item, newName = name)
    },
    onStartRecord = {
      viewModel.sendActionToService(ServiceActionNotification.START)
      viewModel.startRecord(it)
    },
    onOpenSetting = {
      viewModel.showSettingBottomSheet = true
    },
  )

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
      onPathClick = {
        safActivityResult.launch(Uri.EMPTY)
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

  Scaffold(
    contentWindowInsets = WindowInsets(top = 0, bottom = 0),
    modifier = modifier
      .fillMaxSize()
      .getLongPressOffset {
        viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(longPressOffset = it)
      },
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->

    ConstraintLayout(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      val (topSection, recordsControlRef, recordLazyList) = createRefs()

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

        when (state) {
          RecorderScreenUiState.LOADING, RecorderScreenUiState.EMPTY -> {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(top = topSectionSize, bottom = bottomSectionSize),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = if (viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.EMPTY) "No Record" else "Loading",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
              )
            }
          }

          RecorderScreenUiState.DATA -> {
            LazyColumn(
              horizontalAlignment = Alignment.CenterHorizontally,
              contentPadding = PaddingValues(
                bottom = bottomSectionSize,
                top = topSectionSize,
              ),
            ) {
              itemsIndexed(
                items = viewModel.recordDataList,
                key = { _, item -> item.id },
              ) { index, item ->
                RecordListItem(
                  modifier = Modifier.animateItem(),
                  itemIndex = index,
                  currentItemIndex = viewModel.currentIndexClick,
                  data = item,
                  onItemClick = {
                    if (viewModel.showSelectMode) return@RecordListItem
                    viewModel.startPlayAudio(item)
                    viewModel.currentIndexClick = it
                  },
                  onLongItemClick = {
                    if (viewModel.mediaPlayerState.isPlaying) return@RecordListItem
                    viewModel.dropDownMenuState = viewModel.dropDownMenuState.copy(showDropDown = true, itemIndex = it)
                  },
                  isItemSelected = viewModel.selectedItemList.contains(item),
                  onCheckBoxClick = {
                    if (!viewModel.selectedItemList.contains(item)) {
                      viewModel.selectedItemList.add(item)
                    } else {
                      viewModel.selectedItemList.remove(item)
                    }
                  },
                  onSelectMode = viewModel.showSelectMode,
                )

              }

            }
          }
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
              topSectionSize = it.size.height.toDp() + 15.dp
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
              bottomSectionSize = it.size.height.toDp()
            }
          },
        recorderState = { viewModel.recorderState },
        onStartRecordClick = {
          scope.launch {
            val name = viewModel.getDataStoreNameFormat()
            if (name != SettingNameFormat.ASK_ON_RECORD) {
              viewModel.startRecord("")
            } else {
              viewModel.setUiEvent(RecorderScreenUiEvent.NAME_PICKER_DIALOG)
            }
          }
        },
        onDeleteClick = {
          viewModel.setUiEvent(RecorderScreenUiEvent.DELETE_DIALOG)
          viewModel.currentIndexClick = -1
        },
        currentPosition = { viewModel.currentPlayerPosition.toLong() },
        onClosePlayer = {
          viewModel.stopPlayAudio()
          viewModel.currentIndexClick = -1
        },
        onDismissSelectMode = {
          viewModel.showSelectMode = false
          viewModel.deSelectAllItem()
        },
        onShareClick = context::shareItem,
        onResumeRecordClick = viewModel::resumeRecord,
        onStopRecordClick = viewModel::stopRecord,
        onPauseRecordClick = viewModel::pauseRecord,
        currentPlayerState = viewModel::mediaPlayerState,
        onSliderValueChange = viewModel::seekToPosition,
        onPausePlayClick = viewModel::pauseAudio,
        onResumePlayClick = viewModel::resumeAudio,
        onFastBackwardClick = viewModel::fastBackForwardAudio,
        onFastForwardClick = viewModel::fastForwardAudio,
        isOnSelectMode = viewModel.showSelectMode,
        selectedItemCount = { viewModel.selectedItemList.size },
        onDeSelectAll = viewModel::deSelectAllItem,
        onSelectAll = viewModel::selectAllItem,
        onDeleteSelectModeClick = viewModel::deleteRecord,
      )

    }
  }

}

