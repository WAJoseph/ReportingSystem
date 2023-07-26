package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

class BlockedUsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _blockedUsers = MutableLiveData<List<User>>()
    val blockedUsers: LiveData<List<User>> get() = _blockedUsers

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    fun getBlockedUsers(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true

            val users = userRepository.getBlockedUsers(userId)

            _loadingState.value = false

            if (users != null) {
                _blockedUsers.value = users
            } else {
                _errorState.value = "Failed to fetch blocked users."
            }
        }
    }

    fun unblockUser(userId: String, unblockedUserId: String) {
        viewModelScope.launch {
            val success = userRepository.unblockUser(userId, unblockedUserId)

            if (!success) {
                _errorState.value = "Failed to unblock user."
            }
        }
    }
}