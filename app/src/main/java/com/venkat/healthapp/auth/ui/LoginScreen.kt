package com.venkat.healthapp.auth.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.auth.viewmodel.AuthViewModel
import com.venkat.healthapp.auth.data.AuthResult
import com.venkat.healthapp.common.*

@Composable
fun AuthScreen(
    authVm: AuthViewModel,
    onLoggedIn: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }

    if (isLogin) {
        LoginScreen(
            authVm      = authVm,
            onLoggedIn  = onLoggedIn,
            onRegister  = { isLogin = false }
        )
    } else {
        RegisterScreen(
            authVm     = authVm,
            onLoggedIn = onLoggedIn,
            onLogin    = { isLogin = true }
        )
    }
}

// ── Login Screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authVm: AuthViewModel,
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current

    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showPass    by remember { mutableStateOf(false) }
    var showForgot  by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf("") }
    var isLoading   by remember { mutableStateOf(false) }

    val authResult by authVm.authResult.collectAsState()

    LaunchedEffect(authResult) {
        when (val result = authResult) {
            is AuthResult.Success -> { isLoading = false; onLoggedIn() }
            is AuthResult.Error   -> { isLoading = false; errorMsg = result.message }
            is AuthResult.Loading -> isLoading = true
            null -> {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // App logo / title
        Text("💚", fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            "Health Tracker",
            fontSize   = 30.sp,
            fontWeight = FontWeight.Black,
            color      = TextPrimary
        )
        Text(
            "Your personal health companion",
            fontSize = 14.sp,
            color    = TextMuted
        )

        Spacer(Modifier.height(40.dp))

        // Card
        Card(
            Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardDark,
                contentColor   = TextPrimary
            ),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Welcome Back 👋",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Text(
                    "Login to your account",
                    fontSize = 13.sp,
                    color    = TextMuted
                )

                Spacer(Modifier.height(4.dp))

                // Email
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; errorMsg = "" },
                    label         = { Text("Email", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Email, null, tint = Accent)
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction    = ImeAction.Next
                    ),
                    colors = authTextFieldColors()
                )

                // Password
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it; errorMsg = "" },
                    label         = { Text("Password", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = Accent)
                    },
                    trailingIcon  = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                null, tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (showPass)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    colors = authTextFieldColors()
                )

                // Forgot password
                TextButton(
                    onClick  = { showForgot = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = Gold, fontSize = 13.sp)
                }

                // Error
                AnimatedVisibility(visible = errorMsg.isNotBlank()) {
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedAlpha)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, null,
                            tint = RedPill, modifier = Modifier.size(18.dp))
                        Text(errorMsg, color = RedPill, fontSize = 13.sp)
                    }
                }

                // Login button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMsg = "Please fill all fields"
                            return@Button
                        }
                        isLoading = true
                        authVm.loginWithEmail(email.trim(), password)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    enabled  = !isLoading,
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color    = Color.Black,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Login",
                            color      = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize   = 16.sp
                        )
                    }
                }

                // Divider
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(Modifier.weight(1f), color = BorderDark)
                    Text("OR", fontSize = 12.sp, color = TextMuted)
                    HorizontalDivider(Modifier.weight(1f), color = BorderDark)
                }

                // Google Sign In
                OutlinedButton(
                    onClick = {
                        isLoading = true
                        authVm.loginWithGoogle(context)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = BorderStroke(1.dp, BorderDark),
                    enabled  = !isLoading
                ) {
                    Text("🔵  ", fontSize = 18.sp)
                    Text(
                        "Continue with Google",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Register link
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Don't have an account?", color = TextMuted, fontSize = 14.sp)
            TextButton(onClick = onRegister) {
                Text(
                    "Register",
                    color      = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    // Forgot password dialog
    if (showForgot) {
        ForgotPasswordDialog(
            authVm    = authVm,
            onDismiss = { showForgot = false }
        )
    }
}

// ── Register Screen ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authVm: AuthViewModel,
    onLoggedIn: () -> Unit,
    onLogin: () -> Unit
) {
    val context = LocalContext.current
    var name        by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var showPass    by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf("") }
    var isLoading   by remember { mutableStateOf(false) }

    val authResult by authVm.authResult.collectAsState()

    LaunchedEffect(authResult) {
        when (val result = authResult) {
            is AuthResult.Success -> { isLoading = false; onLoggedIn() }
            is AuthResult.Error   -> { isLoading = false; errorMsg = result.message }
            is AuthResult.Loading -> isLoading = true
            null -> {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Text("💚", fontSize = 48.sp)
        Spacer(Modifier.height(8.dp))
        Text("Create Account",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Black,
            color      = TextPrimary)
        Text("Start your health journey",
            fontSize = 13.sp, color = TextMuted)

        Spacer(Modifier.height(24.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardDark,
                contentColor   = TextPrimary
            ),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it; errorMsg = "" },
                    label         = { Text("Full Name", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Person, null, tint = Accent)
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors        = authTextFieldColors()
                )

                // Email
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; errorMsg = "" },
                    label         = { Text("Email", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Email, null, tint = Accent)
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction    = ImeAction.Next
                    ),
                    colors = authTextFieldColors()
                )

                // Password
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it; errorMsg = "" },
                    label         = { Text("Password (min 6 chars)", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = Accent)
                    },
                    trailingIcon  = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                null, tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (showPass)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Next
                    ),
                    colors = authTextFieldColors()
                )

                // Confirm Password
                OutlinedTextField(
                    value         = confirmPass,
                    onValueChange = { confirmPass = it; errorMsg = "" },
                    label         = { Text("Confirm Password", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = Accent)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    colors = authTextFieldColors()
                )

                // Error
                AnimatedVisibility(visible = errorMsg.isNotBlank()) {
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedAlpha)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, null,
                            tint = RedPill, modifier = Modifier.size(18.dp))
                        Text(errorMsg, color = RedPill, fontSize = 13.sp)
                    }
                }

                // Password strength
                if (password.isNotBlank()) {
                    PasswordStrengthBar(password)
                }

                // Register
                Button(
                    onClick = {
                        when {
                            name.isBlank()                -> errorMsg = "Enter your name"
                            email.isBlank()               -> errorMsg = "Enter your email"
                            password.length < 6           -> errorMsg = "Password too short"
                            password != confirmPass       -> errorMsg = "Passwords don't match"
                            else -> {
                                isLoading = true
                                authVm.registerWithEmail(
                                    email.trim(),
                                    password,
                                    name.trim()
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    enabled  = !isLoading,
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color       = Color.Black,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Create Account",
                            color      = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize   = 16.sp
                        )
                    }
                }

                // Google
                OutlinedButton(
                    onClick = { isLoading = true; authVm.loginWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = BorderStroke(1.dp, BorderDark),
                    enabled  = !isLoading
                ) {
                    Text("🔵  ", fontSize = 18.sp)
                    Text("Continue with Google",
                        color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }

                // Terms
                Text(
                    "By registering you agree to our Terms of Service and Privacy Policy",
                    fontSize  = 11.sp,
                    color     = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Already have an account?", color = TextMuted, fontSize = 14.sp)
            TextButton(onClick = onLogin) {
                Text("Login", color = Accent,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ── Password strength bar ─────────────────────────────────────────────────────
@Composable
fun PasswordStrengthBar(password: String) {
    val strength = when {
        password.length >= 12 &&
        password.any { it.isUpperCase() } &&
        password.any { it.isDigit() } &&
        password.any { "!@#\$%^&*".contains(it) } -> 4
        password.length >= 8 &&
        password.any { it.isUpperCase() } &&
        password.any { it.isDigit() }               -> 3
        password.length >= 6 &&
        (password.any { it.isUpperCase() } ||
         password.any { it.isDigit() })             -> 2
        else                                         -> 1
    }

    val (label, color) = when (strength) {
        1    -> "Weak" to RedPill
        2    -> "Fair" to Gold
        3    -> "Good" to AccentBlue
        else -> "Strong" to Accent
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Password strength", fontSize = 11.sp, color = TextMuted)
            Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..4).forEach { i ->
                Box(
                    Modifier.weight(1f).height(4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (i <= strength) color else BorderDark)
                )
            }
        }
    }
}

// ── Forgot password dialog ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    authVm: AuthViewModel,
    onDismiss: () -> Unit
) {
    var email   by remember { mutableStateOf("") }
    var sent    by remember { mutableStateOf(false) }
    var error   by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        title = {
            Text("🔑 Reset Password",
                color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!sent) {
                    Text("Enter your email — we'll send a reset link",
                        color = TextMuted, fontSize = 13.sp)
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it; error = "" },
                        placeholder   = { Text("your@email.com", color = TextMuted) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email),
                        colors        = authTextFieldColors()
                    )
                    if (error.isNotBlank())
                        Text(error, color = RedPill, fontSize = 12.sp)
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✅", fontSize = 40.sp)
                        Text("Reset link sent to $email",
                            color = Accent, fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center)
                        Text("Check your email and click the link to reset your password",
                            color = TextMuted, fontSize = 12.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }
        },
        confirmButton = {
            if (!sent) {
                Button(
                    onClick = {
                        if (email.isBlank()) { error = "Enter email"; return@Button }
                        authVm.sendPasswordReset(email.trim())
                        sent = true
                    },
                    shape  = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Send Reset Link",
                        color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    shape   = RoundedCornerShape(10.dp),
                    colors  = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = if (!sent) {{
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }} else null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Accent,
    unfocusedBorderColor    = BorderDark,
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = Accent,
    focusedContainerColor   = Card2Dark,
    unfocusedContainerColor = Card2Dark
)