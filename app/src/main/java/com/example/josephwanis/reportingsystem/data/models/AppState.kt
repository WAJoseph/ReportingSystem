package com.example.josephwanis.reportingsystem.data.models

sealed class AppState {
    object Loading: AppState()
    data class Error(
        val errorMessage: String
        ): AppState()
    object Idle: AppState()
}