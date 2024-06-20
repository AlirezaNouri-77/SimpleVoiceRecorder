package com.shermanrex.presentation.screen.component.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset

@Composable
fun Modifier.getLongPressOffset(offset: (DpOffset) -> Unit): Modifier {

  return this then pointerInput(Unit) {
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