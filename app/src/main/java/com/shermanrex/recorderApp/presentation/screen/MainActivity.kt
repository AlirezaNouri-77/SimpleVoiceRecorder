package com.shermanrex.recorderApp.presentation.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

      AppRecorderTheme {
        AnimatedContent(
          modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
          targetState = viewmodel.uiState.value,
          label = "") {
          when (it) {
            PermissionScreenUiState.PERMISSION_GRANT -> RecorderScreen()

            PermissionScreenUiState.INITIAL -> Box(
              modifier = Modifier
                .fillMaxSize(),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "Recorder App",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
              )
            }

            PermissionScreenUiState.NO_PERMISSION_GRANT -> {
              PermissionScreen(
                onLocation = { uri ->
                  val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                  this@MainActivity.contentResolver.takePersistableUriPermission(uri, takeFlags)
                  viewmodel.writeDataStoreSavePath(uri.toString())
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
}

