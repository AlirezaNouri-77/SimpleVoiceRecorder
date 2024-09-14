package com.shermanrex.recorderApp.presentation.screen.permision

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) : ViewModel() {

  val uiState = mutableStateOf(PermissionScreenUiState.PERMISSION_GRANT)
  var isAppFirstTimeLaunch by mutableStateOf(false)

  init {
    viewModelScope.launch {
      isAppFirstTimeLaunch = dataStoreManager.getIsFirstTimeAppLaunch.first()
    }
  }

  fun writeDataStoreSavePath(savePath: String) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeSavePath(savePath)
  }

  fun writeFirstTimeAppLaunch(boolean: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeFirstTimeAppLaunch(boolean)
  }

}

enum class PermissionScreenUiState {
  PERMISSION_GRANT, INITIAL
}