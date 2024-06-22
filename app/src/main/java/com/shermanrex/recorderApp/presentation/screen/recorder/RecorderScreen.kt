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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.shermanrex.presentation.screen.component.util.getLongPressOffset
import com.shermanrex.presentation.screen.recorder.component.DeleteDialog
import com.shermanrex.presentation.screen.recorder.component.DialogNamePicker
import com.shermanrex.presentation.screen.recorder.component.bottomSection.BottomSection
import com.shermanrex.recorderApp.data.model.DropDownMenuStateUi
import com.shermanrex.recorderApp.data.model.SettingNameFormat
import com.shermanrex.recorderApp.data.model.notification.ServiceActionNotification
import com.shermanrex.recorderApp.data.model.uiState.RecorderScreenUiState
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

  var showDeleteDialog by remember {
    mutableStateOf(false)
  }
  var showRenameDialog by remember {
    mutableStateOf(false)
  }
  var dropDownMenuState by remember {
    mutableStateOf(DropDownMenuStateUi())
  }
  var lazyListContentPaddingBottom by remember {
    mutableStateOf(0.dp)
  }
  var lazyListContentPaddingTop by remember {
    mutableStateOf(0.dp)
  }
  var showSettingBottomSheet by remember {
    mutableStateOf(false)
  }
  var showNamePickerDialog by remember {
    mutableStateOf(false)
  }
  var currentIndexClick by remember {
    mutableIntStateOf(-1)
  }

  ListDropDownMenu(
    dropDownMenuState = { dropDownMenuState },
    onDismiss = { dropDownMenuState = dropDownMenuState.copy(showDropDown = false) },
    onRenameClick = {
      dropDownMenuState = dropDownMenuState.copy(showDropDown = false)
      showRenameDialog = true
    },
    onDeleteClick = {
      dropDownMenuState = dropDownMenuState.copy(showDropDown = false)
      showDeleteDialog = true
    }
  )


  if (showDeleteDialog) {
    DeleteDialog(
      item = viewModel.recordDataList[dropDownMenuState.itemIndex],
      onDismiss = {
        showDeleteDialog = false
      },
      onAccept = {
        showDeleteDialog = false
        viewModel.deleteRecord(viewModel.recordDataList[dropDownMenuState.itemIndex])
      },
    )
  }

  if (showSettingBottomSheet) {
    Setting(
      sheetSate = sheetState,
      recorderState = viewModel.recorderState,
      onDismiss = { showSettingBottomSheet = false },
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

  if (showRenameDialog) {
    val currentItem = viewModel.recordDataList[dropDownMenuState.itemIndex]
    DialogNamePicker(
      title = "Rename",
      label = "",
      defaultText = currentItem.name,
      positiveText = "Rename",
      negativeText = "Cancel",
      onDismiss = { showRenameDialog = false },
      onPositive = {
        viewModel.renameRecord(targetItem = currentItem, newName = it)
        showRenameDialog = false
      },
    )
  }

  if (showNamePickerDialog) {
    DialogNamePicker(
      title = "Record",
      label = "Enter a name",
      defaultText = "",
      positiveText = "Start",
      negativeText = "Cancel",
      onDismiss = { showNamePickerDialog = false },
      onPositive = {
        viewModel.sendActionToService(ServiceActionNotification.START)
        viewModel.startRecord(it)
      },
    )
  }

  LaunchedEffect(showSettingBottomSheet) {
    if (showSettingBottomSheet) {
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
        dropDownMenuState = dropDownMenuState.copy(longPressOffset = it)
      },
    color = MaterialTheme.colorScheme.background,
  ) {

    ConstraintLayout(
      modifier = Modifier
        .fillMaxSize(),
    ) {

      val (topSection, recordsControlRef, bottomGradient, recordLazyList, centerIndicatorText) = createRefs()

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
            bottom = lazyListContentPaddingBottom,
            top = lazyListContentPaddingTop,
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
                  currentItemIndex = currentIndexClick,
                  data = item,
                  onItemClick = {
                    viewModel.startPlayAudio(item)
                    currentIndexClick = it
                  },
                  onLongItemClick = {
                    dropDownMenuState = dropDownMenuState.copy(showDropDown = true)
                    dropDownMenuState = dropDownMenuState.copy(itemIndex = it)
                  },
                )
              }
            }

            else -> {}
          }

        }
      }

      val centerIndicatorVisibility =
        viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.EMPTY ||
             viewModel.screenRecorderScreenUiState.value == RecorderScreenUiState.LOADING

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
              lazyListContentPaddingTop = it.size.height.toDp() + 15.dp
            }
          },
        amplitudesList = { viewModel.amplitudesList },
        recordTime = { viewModel.recordTime },
        currentAudioSetting = viewModel.currentAudioFormat,
        onSettingClick = { showSettingBottomSheet = true },
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(lazyListContentPaddingBottom / 1.5f)
          .constrainAs(bottomGradient) {
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
          }
          .background(
            brush = Brush.verticalGradient(
              0.2f to Color.Transparent,
              1f to MaterialTheme.colorScheme.primary,
            )
          ),
      ) {}

      BottomSection(
        modifier = Modifier
          .constrainAs(recordsControlRef) {
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
          }
          .onGloballyPositioned {
            with(density) {
              lazyListContentPaddingBottom = it.size.height.toDp()
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
              showNamePickerDialog = true
            }
          }
        },
        onDeleteClick = {
          showDeleteDialog = true
          currentIndexClick = -1
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
          currentIndexClick = -1
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
