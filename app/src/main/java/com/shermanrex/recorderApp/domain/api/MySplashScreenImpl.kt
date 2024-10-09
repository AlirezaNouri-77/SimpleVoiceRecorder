package com.shermanrex.recorderApp.domain.api

import android.app.Activity

interface MySplashScreenImpl {
  fun setSplashScreen(activity: Activity)
  fun setKeepShow(boolean: () -> Boolean)
  fun setAnimationWhenSplashEnd()
}