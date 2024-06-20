package com.shermanrex.presentation.screen.component.util

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.MutableSharedFlow

// disable Ripple effect on clickable Element
object NoRipple : MutableInteractionSource {
  override val interactions = MutableSharedFlow<Interaction>()
  override suspend fun emit(interaction: Interaction) {}
  override fun tryEmit(interaction: Interaction): Boolean {
    return false
  }
}