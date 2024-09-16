package com.shermanrex.recorderApp.presentation.screen.recorder.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shermanrex.presentation.screen.component.util.NoRipple
import com.shermanrex.recorderApp.R
import com.shermanrex.recorderApp.domain.model.DropDownMenuStateUi

@Composable
fun ListDropDownMenu(
  modifier: Modifier = Modifier,
  isSelectedMode: Boolean,
  dropDownMenuState: () -> DropDownMenuStateUi,
  onDismiss: () -> Unit,
  onRenameClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onSelectMode: () -> Unit,
) {
  MaterialTheme(shapes = MaterialTheme.shapes.copy(RoundedCornerShape(20.dp))) {
    DropdownMenu(
      modifier = modifier
        .background(MaterialTheme.colorScheme.primary)
        .wrapContentSize(),
      expanded = dropDownMenuState().showDropDown && !isSelectedMode,
      onDismissRequest = { onDismiss() },
      offset = dropDownMenuState().longPressOffset,
    ) {
      DropdownMenuItem(
        text = { Text(text = "Delete", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
        leadingIcon = {
          Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.icon_delete),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        },
        onClick = {
          onDismiss()
          onDeleteClick()
        },
        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 4.dp),
        interactionSource = NoRipple,
      )
      DropdownMenuItem(
        text = { Text(text = "Rename", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
        leadingIcon = {
          Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.icon_rename),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        },
        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 4.dp),
        onClick = {
          onDismiss()
          onRenameClick()
        },
        interactionSource = NoRipple,
      )
      DropdownMenuItem(
        text = { Text(text = "Select", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
        leadingIcon = {
          Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.icon_rename),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        },
        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 4.dp),
        onClick = {
          onDismiss()
          onSelectMode()
        },
        interactionSource = NoRipple,
      )
    }
  }

}