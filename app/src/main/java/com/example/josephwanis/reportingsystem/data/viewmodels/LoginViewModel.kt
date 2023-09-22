package com.example.josephwanis.reportingsystem.data.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch
import android.content.SharedPreferences

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val error: String) : LoginResult()
}

class LoginViewModel(private val context: Context, private val userRepository: UserRepository, private val appViewModel: AppViewModel) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
        get() = _loginResult

    private val sharedPreferences = context.getSharedPreferences("login_preferences", Context.MODE_PRIVATE)

    fun saveLoginPreferences(email: String, password: String) {
        with(sharedPreferences.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    fun getLoginPreferences(): Pair<String?, String?> {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return Pair(email, password)
    }



    fun loginUser(email: String, password: String) {

        // Cancel the previous job for the LoginAction before starting a new one
        appViewModel.cancelActionJob(AppAction.LoginAction)

        val job = viewModelScope.launch {
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

        // Update the shared job with the new one
        appViewModel.updateSharedJob(job)

    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _loginResult.value = null
    }
}