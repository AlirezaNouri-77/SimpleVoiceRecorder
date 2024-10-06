package com.shermanrex.recorderApp.presentation.screen.permision

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetIsFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSavePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
  private var useCaseWriteSavePath: UseCaseWriteSavePath,
  private var useCaseWriteFirstTimeAppLaunch: UseCaseWriteFirstTimeAppLaunch,
  private var useCaseGetIsFirstTimeAppLaunch: UseCaseGetIsFirstTimeAppLaunch,
) : ViewModel() {

  val uiState = mutableStateOf(PermissionScreenUiState.INITIAL)

  init {
    viewModelScope.launch {
      delay(1500L)
      if (useCaseGetIsFirstTimeAppLaunch().first()) {
        uiState.value = PermissionScreenUiState.NO_PERMISSION_GRANT
      } else {
        uiState.value = PermissionScreenUiState.PERMISSION_GRANT
      }
    }
  }

  fun writeDataStoreSavePath(savePath: String) = viewModelScope.launch {
    useCaseWriteSavePath(savePath)
  }

  fun writeFirstTimeAppLaunch(boolean: Boolean = false) = viewModelScope.launch {
    useCaseWriteFirstTimeAppLaunch(boolean)
  }

}

enum class PermissionScreenUiState {
  PERMISSION_GRANT, NO_PERMISSION_GRANT, INITIAL
}