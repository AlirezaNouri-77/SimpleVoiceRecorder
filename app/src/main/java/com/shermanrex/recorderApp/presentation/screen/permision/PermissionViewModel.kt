package com.shermanrex.recorderApp.presentation.screen.permision

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
  private var dataStoreManager: DataStoreManager,
) : ViewModel() {

  val uiState = mutableStateOf(PermissionScreenUiState.INITIAL)
  var permissionGrant by mutableStateOf(false)
  var saveLocationGrant by mutableStateOf(false)

  fun writeDataStoreSavePath(savePath: String) = viewModelScope.launch(Dispatchers.IO) {
    dataStoreManager.writeSavePath(savePath)
  }

}

enum class PermissionScreenUiState {
  PERMISSION_GRANT, PERMISSION_NOT_GRANT, INITIAL
}