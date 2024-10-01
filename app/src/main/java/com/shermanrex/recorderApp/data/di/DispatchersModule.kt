package com.shermanrex.recorderApp.data.di

import com.shermanrex.recorderApp.data.di.annotation.DispatcherDefault
import com.shermanrex.recorderApp.data.di.annotation.DispatcherIO
import com.shermanrex.recorderApp.data.di.annotation.DispatcherMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

  @Provides
  @DispatcherIO
  fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @DispatcherDefault
  fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

  @Provides
  @DispatcherMain
  fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main

}