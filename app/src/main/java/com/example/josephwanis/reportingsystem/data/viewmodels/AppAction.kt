package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

sealed class AppAction {
    object LoginAction : AppAction()
    object RegistrationAction : AppAction()
    // Add other actions as needed
}

class AppViewModel : ViewModel() {
    // Shared job to be used for coroutine cancellation
    private var sharedJob: Job? = null

    // Function to cancel the shared job based on the action
    fun cancelActionJob(action: AppAction) {
        if (action == AppAction.LoginAction || action == AppAction.RegistrationAction) {
            sharedJob?.cancel()
        }
        // Add more conditions for other actions as needed
    }

    // Function to update the shared job with a new one
    fun updateSharedJob(job: Job) {
        sharedJob = job
    }
}