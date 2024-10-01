package com.shermanrex.recorderApp.data.di

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.di.annotation.ServiceModuleQualifier
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetAudioFormat
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseAppendFileExtension
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetDocumentFileFromUri
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
  @ServiceModuleQualifier
  fun provideUseCaseAppendFileExtension(storageManager: StorageManager): UseCaseAppendFileExtension {
    return UseCaseAppendFileExtension(storageManager)
  }

  @Provides
  @ServiceScoped
  @ServiceModuleQualifier
  fun provideUseCaseGetDocumentFileFromUri(storageManager: StorageManager): UseCaseGetDocumentFileFromUri {
    return UseCaseGetDocumentFileFromUri(storageManager)
  }

  @Provides
  @ServiceScoped
  @ServiceModuleQualifier
  fun provideUseCaseGetAudioFormat(dataStoreManager: DataStoreManager): UseCaseGetAudioFormat {
    return UseCaseGetAudioFormat(dataStoreManager)
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