package com.skilrock.boomlotto.utility

sealed class ResponseStatusError {
    data class NoInternetError(val errorMessageCode: Int) : ResponseStatusError()
    data class TechnicalError(val errorMessageCode: Int) : ResponseStatusError()
}
