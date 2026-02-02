package com.anahjanes.core_domain.model

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(
        val type: ErrorType,
        val message: String? = null,
        val cause: Throwable? = null
    ) : AppResult<Nothing>()
}

enum class ErrorType { Network, Http, Unknown }