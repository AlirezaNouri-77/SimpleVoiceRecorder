package com.shermanrex.recorderApp.presentation.screen.permision

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetIsFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSavePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
  private var useCaseWriteSavePath: UseCaseWriteSavePath,
  private var useCaseWriteFirstTimeAppLaunch: UseCaseWriteFirstTimeAppLaunch,
  private var useCaseGetIsFirstTimeAppLaunch: UseCaseGetIsFirstTimeAppLaunch,
) : ViewModel() {

  private var _uiState = MutableStateFlow(PermissionScreenUiState.INITIAL)
  var uiState = _uiState.asStateFlow()

  private var _removeSplashScreen = MutableStateFlow(false)
  var removeSplashScreen = _removeSplashScreen.asStateFlow()

  init {
    viewModelScope.launch {
      val isFirstTime = useCaseGetIsFirstTimeAppLaunch().first()
      _removeSplashScreen.value = true
      if (isFirstTime) {
        _uiState.value = PermissionScreenUiState.NO_PERMISSION_GRANT
      } else {
        _uiState.value = PermissionScreenUiState.PERMISSION_GRANT
      }
    }
  }

  fun setUiState(permissionScreenUiState: PermissionScreenUiState) {
    _uiState.value = permissionScreenUiState
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