package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val error: String) : LoginResult()
}

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
        get() = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            try {
                val user = userRepository.loginUser(email, password)

                if (user != null) {
                    _loginResult.value = LoginResult.Success(user)
                } else {
                    _loginResult.value = LoginResult.Error("Login failed.")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("An error occurred during login: ${e.message}")
            }
        }
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _loginResult.value = null
    }
}