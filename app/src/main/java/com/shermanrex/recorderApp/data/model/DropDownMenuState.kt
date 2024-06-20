package com.shermanrex.recorderApp.data.model

import androidx.compose.ui.unit.DpOffset

data class DropDownMenuStateUi(
  var itemIndex: Int = 0,
  var showDropDown: Boolean = false,
  var longPressOffset: DpOffset = DpOffset.Zero,
)
