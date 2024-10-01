package com.shermanrex.recorderApp.data.di

import com.shermanrex.recorderApp.data.dataStore.DataStoreManager
import com.shermanrex.recorderApp.data.repository.RecordRepository
import com.shermanrex.recorderApp.data.storage.StorageManager
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetAudioFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetIsFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetNameFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseGetSavePath
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteAudioBitrate
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteAudioFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteFirstTimeAppLaunch
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteNameFormat
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSampleRate
import com.shermanrex.recorderApp.domain.useCase.datastore.UseCaseWriteSavePath
import com.shermanrex.recorderApp.domain.useCase.repository.UseCaseGetRecordByUri
import com.shermanrex.recorderApp.domain.useCase.repository.UseCaseGetRecords
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseAppendFileExtension
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseCreateDocumentFile
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseDeleteRecord
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetDocumentFileFromUri
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetDocumentTreeFileFromUri
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetFileDescriptorByUri
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetFileMetaData
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseGetRenamedRecordName
import com.shermanrex.recorderApp.domain.useCase.storage.UseCaseRenameRecord
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StorageUseCase {

  @Provides
  @Singleton
  fun provideUseCaseAppendFileExtension(storageManager: StorageManager): UseCaseAppendFileExtension {
    return UseCaseAppendFileExtension(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseCreateDocumentFile(storageManager: StorageManager): UseCaseCreateDocumentFile {
    return UseCaseCreateDocumentFile(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseDeleteRecord(storageManager: StorageManager): UseCaseDeleteRecord {
    return UseCaseDeleteRecord(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetDocumentFileFromUri(storageManager: StorageManager): UseCaseGetDocumentFileFromUri {
    return UseCaseGetDocumentFileFromUri(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetFileDescriptorByUri(storageManager: StorageManager): UseCaseGetFileDescriptorByUri {
    return UseCaseGetFileDescriptorByUri(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetDocumentTreeFileFromUri(storageManager: StorageManager): UseCaseGetDocumentTreeFileFromUri {
    return UseCaseGetDocumentTreeFileFromUri(storageManager)
  }

  @Provides
  fun provideUseCaseGetFileMetaData(storageManager: StorageManager): UseCaseGetFileMetaData {
    return UseCaseGetFileMetaData(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseRenameRecord(storageManager: StorageManager): UseCaseRenameRecord {
    return UseCaseRenameRecord(storageManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetRenamedRecordName(storageManager: StorageManager): UseCaseGetRenamedRecordName {
    return UseCaseGetRenamedRecordName(storageManager)
  }

}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryUseCase {
  @Provides
  @ViewModelScoped
  fun provideUseCaseGetRecords(recordRepository: RecordRepository): UseCaseGetRecords {
    return UseCaseGetRecords(recordRepository)
  }

  @Provides
  @ViewModelScoped
  fun provideUseCaseGetRecordByUri(recordRepository: RecordRepository): UseCaseGetRecordByUri {
    return UseCaseGetRecordByUri(recordRepository)
  }
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreUseCase {

  @Provides
  @Singleton
  fun provideUseCaseGetAudioFormat(dataStoreManager: DataStoreManager): UseCaseGetAudioFormat {
    return UseCaseGetAudioFormat(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetIsFirstTimeAppLaunch(dataStoreManager: DataStoreManager): UseCaseGetIsFirstTimeAppLaunch {
    return UseCaseGetIsFirstTimeAppLaunch(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetNameFormat(dataStoreManager: DataStoreManager): UseCaseGetNameFormat {
    return UseCaseGetNameFormat(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseGetSavePath(dataStoreManager: DataStoreManager): UseCaseGetSavePath {
    return UseCaseGetSavePath(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteAudioBitrate(dataStoreManager: DataStoreManager): UseCaseWriteAudioBitrate {
    return UseCaseWriteAudioBitrate(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteAudioFormat(dataStoreManager: DataStoreManager): UseCaseWriteAudioFormat {
    return UseCaseWriteAudioFormat(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteFirstTimeAppLaunch(dataStoreManager: DataStoreManager): UseCaseWriteFirstTimeAppLaunch {
    return UseCaseWriteFirstTimeAppLaunch(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteNameFormat(dataStoreManager: DataStoreManager): UseCaseWriteNameFormat {
    return UseCaseWriteNameFormat(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteSampleRate(dataStoreManager: DataStoreManager): UseCaseWriteSampleRate {
    return UseCaseWriteSampleRate(dataStoreManager)
  }

  @Provides
  @Singleton
  fun provideUseCaseWriteSavePath(dataStoreManager: DataStoreManager): UseCaseWriteSavePath {
    return UseCaseWriteSavePath(dataStoreManager)
  }

}
