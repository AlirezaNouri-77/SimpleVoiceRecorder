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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.shermanrex.recorderApp.data.Constant
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
      navigationBarStyle = SystemBarStyle.light(
        Color.Transparent.toArgb(),
        Color.Transparent.toArgb()
      )
    )
    super.onCreate(savedInstanceState)

    setContent {

      val viewmodel: PermissionViewModel = hiltViewModel()

      if (checkAllPermission(this)) {
        viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_GRANT
      }

      val activityResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { list ->
          val isGrant = list.values.reduce { acc, b -> acc && b }
          if (isGrant) {
            //uiState.value = PermissionScreenUiState.PERMISSION_GRANT
            viewmodel.permissionGrant = true
          } else {
            viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_NOT_GRANT
          }
        }

      val safActivityResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
          val takeFlags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
          it?.let { uri ->
            this.contentResolver.takePersistableUriPermission(uri, takeFlags)
            viewmodel.writeDataStoreSavePath(uri.toString())
            viewmodel.saveLocationGrant = true
          }
        }

      AppRecorderTheme {
        when (viewmodel.uiState.value) {
          PermissionScreenUiState.PERMISSION_GRANT -> {
            RecorderScreen()
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
                    viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_NOT_GRANT
                  },
                )
              },
              onLocation = { safActivityResult.launch(null) },
              moveNextPage = { viewmodel.permissionGrant && viewmodel.saveLocationGrant },
              onMoveToNext = {
                viewmodel.uiState.value = PermissionScreenUiState.PERMISSION_GRANT
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
