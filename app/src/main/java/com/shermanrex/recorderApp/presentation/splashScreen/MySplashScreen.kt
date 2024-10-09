package com.shermanrex.recorderApp.presentation.splashScreen

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shermanrex.recorderApp.domain.api.MySplashScreenImpl

class MySplashScreen : MySplashScreenImpl {

  private lateinit var splashScreen: SplashScreen

  override fun setSplashScreen(activity: Activity) {
    splashScreen = activity.installSplashScreen()
  }

  override fun setKeepShow(boolean: () -> Boolean) {
    splashScreen.setKeepOnScreenCondition { !boolean() }
  }

  override fun setAnimationWhenSplashEnd() {
    splashScreen.setOnExitAnimationListener { provider ->
      val alpha = ObjectAnimator.ofFloat(
        provider.iconView,
        View.ALPHA,
        provider.iconView.alpha,
        0f
      ).apply {
        interpolator = OvershootInterpolator()
        duration = 200
        doOnEnd { provider.remove() }
      }
      val scaleInY = ObjectAnimator.ofFloat(
        provider.iconView,
        View.SCALE_Y,
        provider.iconView.scaleX,
        0f
      ).apply {
        interpolator = OvershootInterpolator()
        duration = 300
        doOnEnd { provider.remove() }
      }
      val scaleInX = ObjectAnimator.ofFloat(
        provider.iconView,
        View.SCALE_X,
        provider.iconView.scaleX,
        0f
      ).apply {
        interpolator = OvershootInterpolator()
        duration = 300
        doOnEnd { provider.remove() }
      }

      scaleInX.start()
      scaleInY.start()
      alpha.start()
    }
  }

}