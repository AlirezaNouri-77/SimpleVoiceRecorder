package com.shermanrex.recorderApp.domain.model.repository


sealed interface RepositoryResult<out T, out E> {
  data object Loading : RepositoryResult<Nothing, Nothing>
  data class Failure<out E : RootFailure>(var error: RootFailure) : RepositoryResult<Nothing, E>
  data class Success<T>(var data: List<T>) : RepositoryResult<T, Nothing>
}

sealed interface RootFailure
enum class Failure : RootFailure {
  Empty,
}