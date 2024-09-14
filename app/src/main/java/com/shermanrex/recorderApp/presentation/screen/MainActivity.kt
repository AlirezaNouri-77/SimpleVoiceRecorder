package com.shermanrex.recorderApp.presentation.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionScreen
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionScreenUiState
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionViewModel
import com.shermanrex.recorderApp.presentation.screen.recorder.RecorderScreen
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
      navigationBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
    )
    super.onCreate(savedInstanceState)

    setContent {

      val viewmodel: PermissionViewModel = hiltViewModel()

      if (viewmodel.isAppFirstTimeLaunch) {
        viewmodel.uiState.value = PermissionScreenUiState.INITIAL
      } else {
        viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_GRANT
      }

      AppRecorderTheme {
        when (viewmodel.uiState.value) {
          PermissionScreenUiState.PERMISSION_GRANT -> RecorderScreen()

          PermissionScreenUiState.INITIAL -> {
            PermissionScreen(
              onLocation = {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                this.contentResolver.takePersistableUriPermission(it, takeFlags)
                viewmodel.writeDataStoreSavePath(it.toString())
              },
              onMoveToNext = {
                viewmodel.writeFirstTimeAppLaunch()
                viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_GRANT
              },
            )
          }

        }

      }
    }
  }
}

