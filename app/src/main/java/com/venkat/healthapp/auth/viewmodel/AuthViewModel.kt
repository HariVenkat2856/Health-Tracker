package com.venkat.healthapp.auth.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.venkat.healthapp.auth.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(application)

    val authState: StateFlow<AuthState> = repo.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult.asStateFlow()

    val currentUser: AppUser? get() = repo.currentUser
    val isLoggedIn: Boolean   get() = repo.isLoggedIn

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            _authResult.value = repo.loginWithEmail(email, password)
        }
    }

    fun registerWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            _authResult.value = repo.registerWithEmail(email, password, name)
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            _authResult.value = repo.loginWithGoogle()
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            repo.sendPasswordReset(email)
        }
    }

    fun logout() {
        repo.logout()
        _authResult.value = null
    }

    fun clearResult() { _authResult.value = null }
}