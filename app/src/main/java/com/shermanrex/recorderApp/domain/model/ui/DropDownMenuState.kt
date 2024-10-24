package com.shermanrex.recorderApp.domain.model.ui

import androidx.compose.ui.unit.DpOffset

data class DropDownMenuStateUi(
  var itemIndex: Int,
  var showDropDown: Boolean,
  var longPressOffset: DpOffset,
) {
  companion object {
    var Empty = DropDownMenuStateUi(
      itemIndex = 0,
      showDropDown = false,
      longPressOffset = DpOffset.Zero,
    )
  }
}
