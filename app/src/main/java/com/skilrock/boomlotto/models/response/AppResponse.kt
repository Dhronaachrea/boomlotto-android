package com.skilrock.boomlotto.models.response

interface AppResponse {
    val errorCode: Int?
    val errorMessage: String?
}