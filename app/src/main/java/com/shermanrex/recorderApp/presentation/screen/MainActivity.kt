package com.shermanrex.recorderApp.presentation.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shermanrex.recorderApp.domain.api.MySplashScreenImpl
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionScreen
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionScreenUiState
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionViewModel
import com.shermanrex.recorderApp.presentation.screen.recorder.RecorderScreen
import com.shermanrex.recorderApp.presentation.splashScreen.MySplashScreen
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), MySplashScreenImpl by MySplashScreen() {
  override fun onCreate(savedInstanceState: Bundle?) {

    val viewmodel: PermissionViewModel by viewModels<PermissionViewModel>()

    setSplashScreen(activity = this)
    setAnimationWhenSplashEnd()
    setKeepShow { viewmodel.removeSplashScreen.value }
    enableEdgeToEdge()

    super.onCreate(savedInstanceState)

    setContent {

      val state = viewmodel.uiState.collectAsStateWithLifecycle().value

      AppRecorderTheme {
        when (state) {
          PermissionScreenUiState.PERMISSION_GRANT -> RecorderScreen()
          PermissionScreenUiState.NO_PERMISSION_GRANT -> PermissionScreen(
            onLocation = { uri ->
              val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
              this@MainActivity.contentResolver.takePersistableUriPermission(uri, takeFlags)
              viewmodel.writeDataStoreSavePath(uri.toString())
            },
            onMoveToNext = {
              viewmodel.writeFirstTimeAppLaunch()
              viewmodel.setUiState(PermissionScreenUiState.PERMISSION_GRANT)
            },
          )
          else -> {}
        }
      }
    }
  }
}