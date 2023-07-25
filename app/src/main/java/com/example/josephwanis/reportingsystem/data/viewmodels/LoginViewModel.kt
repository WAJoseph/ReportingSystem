package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    //Function to handle login using Firebase Authentication
    fun loginUser(email: String, password: String){
        viewModelScope.launch {
            val loginResult = userRepository.loginUser(email,password)
            //Handle the login result(e.g., show a toast or navigate to another screen)
        }
    }

}