package com.shermanrex.recorderApp.domain.model.uiState

enum class RecorderScreenUiState {
  LOADING, EMPTY, DATA
}

enum class RecorderScreenUiEvent {
  INITIAL,
  SAF_PATH,
  DELETE_DIALOG,
  RENAME_DIALOG,
  NAME_PICKER_DIALOG,
}