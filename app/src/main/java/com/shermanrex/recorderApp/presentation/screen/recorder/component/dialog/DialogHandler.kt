package com.shermanrex.recorderApp.presentation.screen.recorder.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.shermanrex.recorderApp.domain.model.record.RecordModel
import com.shermanrex.recorderApp.domain.model.uiState.RecorderScreenUiEvent

@Composable
fun DialogHandler(
  uiEvent: RecorderScreenUiEvent,
  currentItem: () -> RecordModel,
  onDismiss: () -> Unit,
  onDeleteRecord: (RecordModel) -> Unit,
  onRenameRecord: (RecordModel, String) -> Unit,
  onStartRecord: (String) -> Unit,
  onOpenSetting: () -> Unit,
) {

  when (uiEvent) {
    RecorderScreenUiEvent.INITIAL -> {}
    RecorderScreenUiEvent.SAF_PATH -> {
      AlertDialog(
        title = { Text(text = "Save Path not found") },
        text = { Text(text = "cant start record because save path not found please go in app setting and choose save path") },
        confirmButton = {
          Button(
            onClick = {
              onDismiss()
              onOpenSetting()
            },
          ) {
            Text(text = "open setting")
          }
        },
        onDismissRequest = {
          onDismiss()
        },
      )
    }

    RecorderScreenUiEvent.DELETE_DIALOG -> {
      DeleteDialog(
        item = currentItem(),
        onDismiss = { onDismiss() },
        onAccept = {
          onDismiss()
          onDeleteRecord(it)
        },
      )
    }

    RecorderScreenUiEvent.RENAME_DIALOG -> {
      DialogNamePicker(
        title = "Rename",
        label = "",
        defaultText = currentItem().name,
        positiveText = "Rename",
        negativeText = "Cancel",
        onDismiss = { onDismiss() },
        onPositive = {
          onDismiss()
          onRenameRecord(currentItem(), it)
          //   viewModel.renameRecord(targetItem = currentItem, newName = it)
        },
      )
    }

    RecorderScreenUiEvent.NAME_PICKER_DIALOG -> {
      DialogNamePicker(
        title = "Record",
        label = "Enter a name",
        defaultText = "",
        positiveText = "Start",
        negativeText = "Cancel",
        onDismiss = { onDismiss() },
        onPositive = {
          onStartRecord(it)
//        viewModel.sendActionToService(ServiceActionNotification.START)
//        viewModel.startRecord(it)
        },
      )
    }
  }

}
