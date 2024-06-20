package com.shermanrex.recorderApp.data.model

sealed interface RepositoryResult<out T> {
  data object Loading : RepositoryResult<Nothing>
  data object Empty : RepositoryResult<Nothing>
  data class ListData<T>(var data: List<T>) : RepositoryResult<T>
}