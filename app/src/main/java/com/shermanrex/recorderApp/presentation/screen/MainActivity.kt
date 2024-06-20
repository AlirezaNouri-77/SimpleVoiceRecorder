package com.shermanrex.recorderApp.presentation.screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.shermanrex.recorderApp.presentation.screen.permision.PermissionScreen
import com.shermanrex.recorderApp.presentation.screen.recorder.RecorderScreen
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme
import com.shermanrex.recorderApp.data.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
      navigationBarStyle = SystemBarStyle.light(
        Color.Transparent.toArgb(),
        Color.Transparent.toArgb()
      )
    )
    super.onCreate(savedInstanceState)

    val uiState = mutableStateOf(PermissionScreenUiState.INITIAL)
    var permissionGrant by mutableStateOf(false)
    var saveLocationGrant by mutableStateOf(false)

    setContent {

      val viewmodel: AppRecorderViewModel by viewModels()

      if (checkAllPermission(this)) {
        uiState.value = PermissionScreenUiState.PERMISSION_GRANT
      }

      val activityResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { list ->
          val isGrant = list.values.reduce { acc, b -> acc && b }
          if (isGrant) {
            //uiState.value = PermissionScreenUiState.PERMISSION_GRANT
            permissionGrant = true
          } else {
            uiState.value = PermissionScreenUiState.PERMISSION_NOT_GRANT
          }
        }

      val safActivityResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
          val takeFlags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
          it?.let { uri ->
            this.contentResolver.takePersistableUriPermission(uri, takeFlags)
            viewmodel.saveDataStore(uri.toString())
            saveLocationGrant = true
          }
        }

      AppRecorderTheme {
        when (uiState.value) {
          PermissionScreenUiState.PERMISSION_GRANT -> {
            RecorderScreen(
              modifier = Modifier,
              viewModel = viewmodel,
            )
          }

          PermissionScreenUiState.PERMISSION_NOT_GRANT -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text(text = "No Permission", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          PermissionScreenUiState.INITIAL -> {
            PermissionScreen(
              modifier = Modifier
                .displayCutoutPadding()
                .systemBarsPadding(),
              onPermission = {
                requestPermission(
                  this@MainActivity,
                  activityResult,
                  notGrant = {
                    uiState.value = PermissionScreenUiState.PERMISSION_NOT_GRANT
                  },
                )
              },
              onLocation = { safActivityResult.launch(null) },
              moveNextPage = { permissionGrant && saveLocationGrant },
              onMoveToNext = {
                uiState.value = PermissionScreenUiState.PERMISSION_GRANT
              },
            )
          }

        }

      }
    }
  }
}

private fun requestPermission(
  context: Context,
  launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
  notGrant: () -> Unit,
) {
  if (
    checkAllPermission(context)
  ) {
    notGrant()
  } else {
    launcher.launch(Constant.permissionList)
  }
}

private fun checkAllPermission(context: Context): Boolean {
  return Constant.permissionList.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
  }
}

private enum class PermissionScreenUiState {
  PERMISSION_GRANT, PERMISSION_NOT_GRANT, INITIAL
}
