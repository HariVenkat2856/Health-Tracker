package com.venkat.healthapp.auth.data

import com.google.firebase.auth.FirebaseUser

data class AppUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun FirebaseUser.toAppUser() = AppUser(
    uid         = uid,
    email       = email ?: "",
    displayName = displayName ?: email?.substringBefore("@") ?: "User",
    photoUrl = photoUrl?.toString() ?: ""
)

sealed class AuthState {
    object Loading      : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: AppUser) : AuthState()
}

sealed class AuthResult {
    data class Success(val user: AppUser) : AuthResult()
    data class Error(val message: String)  : AuthResult()
    object Loading                         : AuthResult()
}