package com.shermanrex.recorderApp.presentation.screen.recorder.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.presentation.util.bounce


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorderTopBar(
  modifier: Modifier = Modifier,
  onSettingClick: () -> Unit,
) {
  TopAppBar(
    modifier = modifier,
    colors = TopAppBarDefaults.topAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary,
      containerColor = MaterialTheme.colorScheme.primary,
    ),
    title = {
      Text(
        text = "App Recorder",
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
      )
    },
    actions = {
      IconButton(
        modifier = Modifier
          .bounce(),
        onClick = { onSettingClick() },
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_settings),
          contentDescription = "",
          modifier = Modifier.size(
            24.dp
          ),
        )
      }
    }
  )
}