package com.venkat.healthapp.auth.data

import android.content.Context
import androidx.credentials.*
import androidx.credentials.exceptions.*
import com.google.android.libraries.identity.googleid.*
import com.google.firebase.auth.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    // ── Current user state ────────────────────────────────────────────────────
    val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(
                if (user != null) AuthState.Authenticated(user.toAppUser())
                else AuthState.Unauthenticated
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
    init {
        auth.firebaseAuthSettings
            .setAppVerificationDisabledForTesting(true)
    }
    val currentUser: AppUser?
        get() = auth.currentUser?.toAppUser()

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    // ── Email registration ────────────────────────────────────────────────────
    suspend fun registerWithEmail(
        email: String,
        password: String,
        name: String
    ): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            result.user?.updateProfile(profileUpdates)?.await()

            // Send verification email
            result.user?.sendEmailVerification()?.await()

            AuthResult.Success(result.user!!.toAppUser())
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("Email already registered. Please login.")
        } catch (e: FirebaseAuthWeakPasswordException) {
            AuthResult.Error("Password too weak. Use at least 6 characters.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Invalid email address.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed. Try again.")
        }
    }

    // ── Email login ───────────────────────────────────────────────────────────
    suspend fun loginWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user!!.toAppUser())
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("No account found with this email.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Wrong password. Try again.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed. Try again.")
        }
    }

    // ── Google Sign In ────────────────────────────────────────────────────────
    suspend fun loginWithGoogle(activityContext: Context): AuthResult {
        return try {
            val credentialManager = CredentialManager.create(activityContext) // ← use activityContext
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request) // ← here too
            handleGoogleCredential(result.credential)
        } catch (e: GetCredentialCancellationException) {
            AuthResult.Error("Sign in cancelled")
        } catch (e: NoCredentialException) {
            // No Google accounts on device or none previously authorized
            AuthResult.Error("No Google account found. Please add a Google account to your device.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign in failed")
        }
    }

    private suspend fun handleGoogleCredential(credential: Credential): AuthResult {
        return if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdToken = GoogleIdTokenCredential
                .createFrom(credential.data).idToken
            val firebaseCredential = GoogleAuthProvider
                .getCredential(googleIdToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            AuthResult.Success(result.user!!.toAppUser())
        } else {
            AuthResult.Error("Invalid credential type")
        }
    }

    // ── Forgot password ───────────────────────────────────────────────────────
    suspend fun sendPasswordReset(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(AppUser("", email, ""))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to send reset email")
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() = auth.signOut()

    // ── Delete account ────────────────────────────────────────────────────────
    suspend fun deleteAccount(): AuthResult {
        return try {
            auth.currentUser?.delete()?.await()
            AuthResult.Success(AppUser("", "", ""))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to delete account")
        }
    }

    companion object {
        // Replace with your Web Client ID from Firebase Console
        const val WEB_CLIENT_ID = "807620171706-oqghfs9b7qoo64vvimfphv439mgnekuj.apps.googleusercontent.com"
    }

    suspend fun updateDisplayName(name: String): AuthResult {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            AuthResult.Success(auth.currentUser!!.toAppUser())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update name")
        }
    }
}