package com.shermanrex.recorderApp.presentation.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset

@Composable
fun Modifier.getLongPressOffset(offset: (DpOffset) -> Unit): Modifier {

  return this then Modifier.pointerInput(Unit) {
    this.awaitEachGesture {
      val id = this.awaitFirstDown(false).id
      val long = this.awaitLongPressOrCancellation(id)
      if (long?.pressed == true) {
        offset(
          DpOffset(
            x = long.position.x.toDp(),
            y = long.position.y.toDp()
          )
        )
      }
    }
  }

}

@Composable
fun Modifier.bounce(): Modifier {
  val buttonState = remember { mutableStateOf(ButtonState.Idle) }
  val scale by animateFloatAsState(
    targetValue = if (buttonState.value == ButtonState.Pressed) 0.70f else 1f,
    label = "",
    finishedListener = {
      buttonState.value = ButtonState.Idle
    },
  )
  return this then Modifier.graphicsLayer {
    this.scaleX = scale
    this.scaleY = scale
  }
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = {},
    )
    .pointerInput(buttonState) {
      this.awaitEachGesture {
        val down = awaitFirstDown(false)
        if (down.pressed) {
          buttonState.value = ButtonState.Pressed
        }
      }
    }
}

private enum class ButtonState { Pressed, Idle }