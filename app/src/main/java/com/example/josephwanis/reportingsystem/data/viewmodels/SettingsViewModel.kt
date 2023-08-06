package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean>
        get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    // Keep the necessary fields for the text fields
    private val _displayNameState = MutableLiveData("")
    val displayNameState: LiveData<String>
        get() = _displayNameState

    private val _emailState = MutableLiveData("")
    val emailState: LiveData<String>
        get() = _emailState

    private val _passwordState = MutableLiveData("")
    val passwordState: LiveData<String>
        get() = _passwordState

    // Add functions to handle text field changes
    fun onDisplayNameChange(displayName: String) {
        _displayNameState.value = displayName
    }

    fun onEmailChange(email: String) {
        _emailState.value = email
    }

    fun onPasswordChange(password: String) {
        _passwordState.value = password
    }

    fun updateUserProfile(userId: String, displayName: String, profileImageUri: String?) {
        viewModelScope.launch {
            val success = userRepository.updateUserProfile(userId, displayName, profileImageUri)
            if (success) {
                _updateSuccess.value = true
            } else {
                _errorMessage.value = "Failed to update user profile."
            }
        }
    }

    // Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}