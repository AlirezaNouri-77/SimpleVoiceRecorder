package com.shermanrex.recorderApp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.di.annotation.DispatcherIO
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.data.service.connection.MediaPlayerServiceConnection
import com.shermanrex.recorderApp.data.service.connection.MediaRecorderServiceConnection
import com.shermanrex.recorderApp.data.storage.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRecordModule {

  @Provides
  @Singleton
  fun provideDataStoreManager(dataStore: DataStore<Preferences>, @DispatcherIO coroutineDispatcher: CoroutineDispatcher): DataStoreManager {
    return DataStoreManager(datastore = dataStore, dispatcherIO = coroutineDispatcher)
  }

  @Provides
  @Singleton
  fun provideRecordRepository(@ApplicationContext context: Context, @DispatcherIO coroutineDispatcher: CoroutineDispatcher, storageManager: StorageManager): RecordRepository {
    return RecordRepository(context = context, storageManager = storageManager, dispatcherIO = coroutineDispatcher)
  }

  @Provides
  @Singleton
  fun provideStorageManager(@ApplicationContext context: Context, @DispatcherIO coroutineDispatcher: CoroutineDispatcher): StorageManager {
    return StorageManager(context = context, dispatcherIO = coroutineDispatcher)
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