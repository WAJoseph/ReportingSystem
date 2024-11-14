package com.example.josephwanis.reportingsystem.data.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatbotViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
            return ChatbotViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}