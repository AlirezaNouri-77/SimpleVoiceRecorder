package com.shermanrex.recorderApp.domain.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.data.repository.StorageManager
import com.shermanrex.recorderApp.data.service.connection.MediaPlayerServiceConnection
import com.shermanrex.recorderApp.data.service.connection.MediaRecorderServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRecordModule {

  @Provides
  @Singleton
  fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
    return DataStoreManager(datastore = provideDataStore(context))
  }

  @Provides
  @Singleton
  fun provideRecordRepository(@ApplicationContext context: Context): RecordRepository {
    return RecordRepository(context = context , storageManager = provideStorageManager(context))
  }

  @Provides
  @Singleton
  fun provideStorageManager(@ApplicationContext context: Context): StorageManager {
    return StorageManager(context = context)
  }

  @Provides
  @Singleton
  fun provideMediaPlayerController(@ApplicationContext context: Context): MediaPlayerServiceConnection {
    return MediaPlayerServiceConnection(context = context)
  }

  @Provides
  @Singleton
  fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
      produceFile = { context.preferencesDataStoreFile("settings") }
    )
  }

  @Provides
  @Singleton
  fun provideMediaRecorderServiceConnection(@ApplicationContext context: Context): MediaRecorderServiceConnection {
    return MediaRecorderServiceConnection(context = context)
  }

}