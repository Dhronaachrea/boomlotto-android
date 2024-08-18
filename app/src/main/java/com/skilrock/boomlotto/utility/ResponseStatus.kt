package com.skilrock.boomlotto.utility

sealed class ResponseStatus<out T> {
    data class Success<T>(val response: T) : ResponseStatus<T>()
    data class Error(val errorMessage: String, val errorCode: Int) : ResponseStatus<Nothing>()
    data class TechnicalError(val errorMessageCode: Int) : ResponseStatus<Nothing>()
}
