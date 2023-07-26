package com.example.josephwanis.reportingsystem.data.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseCloudMessagingManager
import kotlinx.coroutines.launch

class PushNotificationViewModel : ViewModel() {

    private val _subscribeSuccess = MutableLiveData<Boolean>()
    val subscribeSuccess: LiveData<Boolean>
        get() = _subscribeSuccess

    private val _unsubscribeSuccess = MutableLiveData<Boolean>()
    val unsubscribeSuccess: LiveData<Boolean>
        get() = _unsubscribeSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun subscribeToTopic(topic: String) {
        viewModelScope.launch {
            val success = FirebaseCloudMessagingManager.subscribeToTopic(topic)
            if (success) {
                _subscribeSuccess.value = true
            } else {
                _errorMessage.value = "Failed to subscribe to topic: $topic"
            }
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        viewModelScope.launch {
            val success = FirebaseCloudMessagingManager.unsubscribeFromTopic(topic)
            if (success) {
                _unsubscribeSuccess.value = true
            } else {
                _errorMessage.value = "Failed to unsubscribe from topic: $topic"
            }
        }
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}