package com.shermanrex.recorderApp.domain.di

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.shermanrex.recorderApp.presentation.notification.MyNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

  @Provides
  @ServiceScoped
  fun provideNotificationManager(@ApplicationContext context: Context): MyNotificationManager {
    return MyNotificationManager(context)
  }

  @Provides
  @ServiceScoped
  fun provideMediaRecorder(@ApplicationContext context: Context): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      MediaRecorder(context)
    } else {
      MediaRecorder()
    }
  }


}