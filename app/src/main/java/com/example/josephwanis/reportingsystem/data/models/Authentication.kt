package com.example.josephwanis.reportingsystem.data.models

sealed class AuthenticationState {
    object Authenticated: AuthenticationState()
    object Unauthenticated: AuthenticationState()
}