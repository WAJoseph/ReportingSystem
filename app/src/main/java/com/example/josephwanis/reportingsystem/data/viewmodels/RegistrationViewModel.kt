package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

sealed class RegistrationResult {
    object Loading : RegistrationResult()
    data class Success(val user: User) : RegistrationResult()
    data class Error(val error: String) : RegistrationResult()
}

class RegistrationViewModel(private val userRepository: UserRepository, private val chatRepository: ChatRepository,private val appViewModel: AppViewModel) : ViewModel() {

    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult>
        get() = _registrationResult


    fun registerUser(email: String, password: String, displayName: String, isKnownUser: Boolean) {


        // Cancel the previous job for the RegistrationAction before starting a new one
        appViewModel.cancelActionJob(AppAction.RegistrationAction)

        val job = viewModelScope.launch {
            _registrationResult.value = RegistrationResult.Loading
            try {
                val user = userRepository.registerUser(email, password, displayName, isKnownUser)

                if (user != null) {
                    _registrationResult.value = RegistrationResult.Success(user)

                    if (isKnownUser) {
                        chatRepository.createChatSessionsWithAllUsers(user.userId)
                        chatRepository.sendWelcomeMessageToAllUsers(user.userId)
                    }else{
                        chatRepository.createChatSessionsWithKnownUsers(user.userId)
                    }
                } else {
                    _registrationResult.value = RegistrationResult.Error("Registration failed.")
                }
            } catch (e: Exception) {
                _registrationResult.value = when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        RegistrationResult.Error("Email is already registered. Please use a different email.")
                    }
                    is FirebaseNetworkException -> {
                        RegistrationResult.Error("Network error. Please check your internet connection and try again.")
                    }
                    else -> {
                        RegistrationResult.Error("An unexpected error occurred during registration. Please try again later.")
                    }
                }
            }
        }

        // Update the shared job with the new one
        appViewModel.updateSharedJob(job)

    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _registrationResult.value = null
    }
}