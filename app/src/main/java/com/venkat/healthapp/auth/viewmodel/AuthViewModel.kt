package com.venkat.healthapp.auth.viewmodel

import android.app.Application
import android.content.Context
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


    private val _currentUser = MutableStateFlow<AppUser?>(repo.currentUser)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean get() = repo.isLoggedIn
    init {
        // Keep _currentUser in sync with authState
        viewModelScope.launch {
            authState.collect { state ->
                _currentUser.value = when (state) {
                    is AuthState.Authenticated -> state.user
                    else -> null
                }
            }
        }
    }
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

    fun loginWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            _authResult.value = repo.loginWithGoogle(activityContext)
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            repo.sendPasswordReset(email)
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            val result = repo.updateDisplayName(name)
            if (result is AuthResult.Success) {
                _currentUser.value = _currentUser.value?.copy(displayName = name)
            }
        }
    }


    fun logout() {
        repo.logout()
        _authResult.value = null
        _currentUser.value = null
    }

    fun clearResult() { _authResult.value = null }



}