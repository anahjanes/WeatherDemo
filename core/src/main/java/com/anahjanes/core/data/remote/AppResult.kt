package com.anahjanes.core.data.remote

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(
        val type: ErrorType,
        val message: String? = null,
        val cause: Throwable? = null
    ) : AppResult<Nothing>()
}

enum class ErrorType { Network, Http, Unknown }