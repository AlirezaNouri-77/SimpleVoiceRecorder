package com.shermanrex.recorderApp.presentation.screen.permision

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun PermissionScreen(
  modifier: Modifier = Modifier,
  onLocation: (Uri) -> Unit,
  onMoveToNext: () -> Unit,
) {

  var isNotificationPermission by remember {
    var isSdk = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    mutableStateOf(isSdk)
  }
  var isMicrophonePermission by remember {
    mutableStateOf(false)
  }
  var isSafLocationGrant by remember {
    mutableStateOf(false)
  }

  val notificationActivityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { permission ->
    isNotificationPermission = permission
  }

  val microphoneActivityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { permission ->
    isMicrophonePermission = permission
  }

  val safActivityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
    it?.let { uri ->
      onLocation(uri)
      isSafLocationGrant = true
    }
  }

  ConstraintLayout(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {

    val (appName, permissionSection, getStartButton) = createRefs()
    val topGuideLine = createGuidelineFromTop(0.2f)
    val centerGuideLine = createGuidelineFromTop(0.5f)

    Text(
      modifier = Modifier.constrainAs(appName) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(topGuideLine)
      },
      text = "Recorder App", fontSize = 34.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onPrimary,
    )

    Column(
      modifier = Modifier
        .padding(15.dp)
        .constrainAs(permissionSection) {
          top.linkTo(centerGuideLine)
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          bottom.linkTo(centerGuideLine)
        },
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "Permissions",
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Text(
        text = "Request the necessary permissions required for the app to work properly",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
      )

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Button(
          onClick = {
            notificationActivityResult.launch(Manifest.permission.POST_NOTIFICATIONS)
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isNotificationPermission) Color.Green.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
        ) {
          Text(
            text = "Notification", fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
          )
        }
      }

      Button(
        onClick = {
          microphoneActivityResult.launch(Manifest.permission.RECORD_AUDIO)
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isMicrophonePermission) Color.Green.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
      ) {
        Text(
          text = "Microphone", fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      }

      Spacer(Modifier.height(10.dp))

      Text(
        text = "Records save location",
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Text(
        text = "Select a location for save records",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Button(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        onClick = { safActivityResult.launch(null) },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isSafLocationGrant) Color.Green.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
      ) {
        Text(
          text = "Grant", fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      }
    }
    Button(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .constrainAs(getStartButton) {
          bottom.linkTo(parent.bottom, margin = 15.dp)
          start.linkTo(parent.start)
          end.linkTo(parent.end)
        },
      onClick = {
        if (isMicrophonePermission && isNotificationPermission && isSafLocationGrant) onMoveToNext()
      },
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
      ),
    ) {
      Text(
        text = "Get start", fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
      )
    }

  }


}

@Preview(
  showBackground = true,
  showSystemUi = true,
)
@Preview(
  showSystemUi = true,
  showBackground = true,
  uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
  AppRecorderTheme {
    PermissionScreen(
      onLocation = {},
      onMoveToNext = {})
  }
}