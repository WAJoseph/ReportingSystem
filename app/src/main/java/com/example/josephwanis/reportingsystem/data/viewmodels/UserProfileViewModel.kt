package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

class UserProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User>
        get() = _userProfile

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean>
        get() = _updateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserProfile(userId)
            if (user != null) {
                _userProfile.value = user
            } else {
                _errorMessage.value = "Failed to fetch user profile."
            }
        }
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

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}