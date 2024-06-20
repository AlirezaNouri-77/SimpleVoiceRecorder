package com.shermanrex.recorderApp.presentation.screen.permision

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import com.shermanrex.recorderApp.data.model.AudioFormat
import com.shermanrex.recorderApp.data.model.RecordAudioSetting
import com.shermanrex.recorderApp.data.model.RecorderState
import com.shermanrex.recorderApp.data.model.SettingNameFormat
import com.shermanrex.recorderApp.presentation.screen.setting.BottomSheetContent
import com.shermanrex.recorderApp.presentation.ui.theme.AppRecorderTheme

@Composable
fun PermissionScreen(
  modifier: Modifier = Modifier,
  moveNextPage: () -> Boolean,
  onPermission: () -> Unit,
  onLocation: () -> Unit,
  onMoveToNext: () -> Unit,
) {

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
    ) {
      Text(
        text = "Permissions",
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Text(
        text = "Request needed permission that need to app work properly",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Button(
        onClick = { onPermission() },
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
      ) {
        Text(
          text = "Grant", fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      }

      Spacer(modifier = Modifier.height(15.dp))
      Text(
        text = "Records save location",
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Text(
        text = "Choose a location for save records",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      Button(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        onClick = { onLocation() },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
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
      onClick = { if (moveNextPage()) onMoveToNext() },
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
      onPermission = {},
      onLocation = {},
      moveNextPage = { false },
      onMoveToNext = {})
  }
}