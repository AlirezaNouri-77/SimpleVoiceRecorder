package com.shermanrex.recorderApp.domain.model

sealed interface RepositoryResult<out T> {
  data object Loading : RepositoryResult<Nothing>
  data object Empty : RepositoryResult<Nothing>
  data class ListData<T>(var data: List<T>) : RepositoryResult<T>
}