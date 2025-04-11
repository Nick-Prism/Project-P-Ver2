package com.example.collegeeventplanner.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val error: Exception? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String, error: Exception? = null, data: T? = null) : Resource<T>(data, message, error)
    class Loading<T>(data: T? = null) : Resource<T>(data)

    fun isSuccess() = this is Success<T>
    fun isLoading() = this is Loading<T>
    fun isError() = this is Error<T>
}