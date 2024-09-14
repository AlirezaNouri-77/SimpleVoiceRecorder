package com.shermanrex.recorderApp.data.di

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.presentation.notification.MyNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Qualifier

@Qualifier
annotation class ServiceModuleQualifier

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
  @ServiceModuleQualifier
  fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
      produceFile = { context.preferencesDataStoreFile("settings") }
    )
  }

  @Provides
  @ServiceScoped
  @ServiceModuleQualifier
  fun provideStorageManager(@ApplicationContext context: Context): StorageManager {
    return StorageManager(context = context)
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