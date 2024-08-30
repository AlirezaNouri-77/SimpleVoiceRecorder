package com.shermanrex.presentation.screen.recorder.component.bottomSection

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.presentation.screen.component.util.NoRipple
import com.shermanrex.presentation.screen.component.util.bounce
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.domain.model.RecorderState

@Composable
fun Recorder(
  modifier: Modifier = Modifier,
  recorderState: () -> RecorderState,
  onPauseRecordClick: () -> Unit,
  onStopRecordClick: () -> Unit,
  onStartRecordClick: () -> Unit,
  onResumeRecordClick: () -> Unit,
) {

  val bottomColor = if (isSystemInDarkTheme()) Color.White else Color.Black
  var onButtonClick by remember {
    mutableStateOf(false)
  }
  val animateColorButton = remember {
    Animatable(bottomColor)
  }
  LaunchedEffect(recorderState()) {
    when (recorderState()) {
      RecorderState.RECORDING -> animateColorButton.animateTo(Color(0xFFED0909), tween(600))
      RecorderState.STOP -> animateColorButton.animateTo(bottomColor, tween(600))
      RecorderState.PAUSE -> animateColorButton.animateTo(Color(0xFF1F75FF), tween(600))
      else -> {}
    }
  }
  val buttonText = when (recorderState()) {
    RecorderState.RECORDING -> "Recording"
    RecorderState.STOP -> "Record"
    RecorderState.PAUSE -> "Resume"
    RecorderState.IDLE -> "Record"
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentSize()
      .mandatorySystemGesturesPadding()
      .padding(horizontal = 10.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary,
    ),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(
      defaultElevation = 10.dp
    ),
  ) {

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally),
    ) {

      IconButton(
        modifier = Modifier
          .bounce()
          .weight(0.2f, false),
        onClick = {
          onPauseRecordClick()
          onButtonClick = true
        },
        interactionSource = NoRipple,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = Color.Transparent,
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
      ) {
        Icon(
          modifier = Modifier.size(19.dp),
          painter = painterResource(id = R.drawable.pause),
          contentDescription = "",
        )
      }

      ElevatedButton(
        modifier = Modifier.weight(0.5f, true),
        onClick = {
          if (recorderState() == RecorderState.PAUSE) {
            onResumeRecordClick()
          } else {
            onStartRecordClick()
          }
        },
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = animateColorButton.value,
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
          defaultElevation = 10.dp,
          pressedElevation = 5.dp
        )
      ) {
        Text(
          text = buttonText,
          fontWeight = FontWeight.SemiBold,
          fontSize = 19.sp,
        )
      }

      IconButton(
        modifier = Modifier
          .bounce()
          .weight(0.2f, false),
        onClick = {
          onStopRecordClick()
        },
        interactionSource = NoRipple,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = Color.Transparent,
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
      ) {
        Icon(
          modifier = Modifier.size(19.dp),
          painter = painterResource(id = R.drawable.stop),
          contentDescription = "",
        )
      }
    }
  }
}