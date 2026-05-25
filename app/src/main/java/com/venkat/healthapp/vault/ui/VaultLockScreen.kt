package com.venkat.healthapp.vault.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.common.*
import com.venkat.healthapp.vault.data.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.core.tween

// ── Vault entry point — decides which screen to show ─────────────────────────
@Composable
fun VaultEntryPoint(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    val isSetup = remember { VaultPinManager.isSetupDone(context) }

    if (!isSetup) {
        VaultSetupScreen(onSetupComplete = onUnlocked)
    } else {
        VaultLockScreen(onUnlocked = onUnlocked)
    }
}

// ── Setup screen — first time ─────────────────────────────────────────────────
@Composable
fun VaultSetupScreen(onSetupComplete: () -> Unit) {
    val context  = LocalContext.current
    var step     by remember { mutableIntStateOf(1) }  // 1=pin, 2=confirm, 3=hint+recovery
    var pin      by remember { mutableStateOf("") }
    var confirm  by remember { mutableStateOf("") }
    var hint     by remember { mutableStateOf("") }
    var recovery by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(BgDark).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(40.dp))
        Text("🔐", fontSize = 64.sp)
        Text("Setup Vault PIN",
            fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)

        // Step indicator
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..3).forEach { i ->
                Box(
                    Modifier.size(if (step == i) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i <= step) Accent else BorderDark)
                )
            }
        }

        when (step) {
            1 -> {
                Text("Create a 6-digit PIN",
                    color = TextMuted, fontSize = 14.sp)
                PinDots(pin)
                PinKeypad(
                    pin      = pin,
                    onPin    = { pin = it },
                    maxLen   = 6,
                    onSubmit = {
                        if (pin.length == 6) { step = 2; error = "" }
                        else error = "Enter 6 digits"
                    }
                )
            }
            2 -> {
                Text("Confirm your PIN",
                    color = TextMuted, fontSize = 14.sp)
                PinDots(confirm)
                PinKeypad(
                    pin      = confirm,
                    onPin    = { confirm = it },
                    maxLen   = 6,
                    onSubmit = {
                        if (confirm == pin) { step = 3; error = "" }
                        else { error = "PINs don't match! Try again"; confirm = "" }
                    }
                )
            }
            3 -> {
                Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Recovery Setup",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "If you forget your PIN, you need these to recover access. " +
                        "This is stored securely — only you know it.",
                        color = TextMuted, fontSize = 13.sp
                    )

                    // PIN Hint
                    OutlinedTextField(
                        value         = hint,
                        onValueChange = { hint = it },
                        label         = { Text("PIN Hint (visible on lock screen)", color = TextMuted) },
                        placeholder   = { Text("e.g. My birth year + lucky number", color = TextMuted) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = vaultTextFieldColors()
                    )

                    // Recovery question (fixed — mother's name)
                    Text("Recovery Answer",
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(
                        "❓ What is your mother's first name?",
                        fontSize = 13.sp, color = Gold
                    )
                    OutlinedTextField(
                        value         = recovery,
                        onValueChange = { recovery = it },
                        label         = { Text("Answer (case insensitive)", color = TextMuted) },
                        placeholder   = { Text("Your mother's first name", color = TextMuted) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = vaultTextFieldColors()
                    )

                    // Warning
                    Box(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldAlpha)
                            .border(1.dp, Gold.copy(0.4f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            "⚠️ Remember these:\n" +
                            "• Your 6-digit PIN\n" +
                            "• Your PIN hint\n" +
                            "• Your mother's name\n\n" +
                            "Without these, vault data cannot be recovered.\n" +
                            "This app is only for you — no cloud backup.",
                            fontSize = 13.sp, color = Gold
                        )
                    }

                    Button(
                        onClick = {
                            if (hint.isBlank()) { error = "Add a PIN hint"; return@Button }
                            if (recovery.isBlank()) { error = "Add recovery answer"; return@Button }
                            VaultPinManager.setupPin(context, pin, hint, recovery)
                            onSetupComplete()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Accent),
                        enabled  = hint.isNotBlank() && recovery.isNotBlank()
                    ) {
                        Icon(Icons.Default.Lock, null,
                            tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Create Vault",
                            color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }
        }

        if (error.isNotBlank()) {
            Text(error, color = RedPill, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Lock screen ───────────────────────────────────────────────────────────────
@Composable
fun VaultLockScreen(onUnlocked: () -> Unit) {
    val context     = LocalContext.current
    var pin         by remember { mutableStateOf("") }
    var error       by remember { mutableStateOf("") }
    var showHint    by remember { mutableStateOf(false) }
    var showRecovery by remember { mutableStateOf(false) }

    // Shake animation on wrong PIN
    val shakeAnim   = remember { Animatable(0f) }

    Column(
        Modifier.fillMaxSize().background(BgDark).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(60.dp))

        // Lock icon with glow
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(100.dp).clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(AccentAlpha, Color.Transparent))
                    )
            )
            Text("🔐", fontSize = 52.sp)
        }

        Text("Secure Vault",
            fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Enter your PIN to access",
            color = TextMuted, fontSize = 14.sp)

        Spacer(Modifier.height(8.dp))

        // PIN dots with shake
        Box(
            Modifier.offset(x = shakeAnim.value.dp)
        ) {
            PinDots(pin)
        }

        // Error
        AnimatedVisibility(visible = error.isNotBlank()) {
            Text(error, color = RedPill, fontSize = 13.sp,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }

        // Keypad
        val scope = rememberCoroutineScope()

        PinKeypad(
            pin      = pin,
            onPin    = { pin = it },
            maxLen   = 6,
            onSubmit = {

                when (val result = VaultPinManager.verifyPin(context, pin)) {

                    is PinResult.SUCCESS -> onUnlocked()

                    is PinResult.WRONG -> {

                        error = "Wrong PIN! ${result.attemptsLeft} attempts left"
                        pin = ""

                        // Shake animation
                        scope.launch {

                            repeat(3) {

                                shakeAnim.animateTo(
                                    targetValue = 12f,
                                    animationSpec = tween(80)
                                )

                                shakeAnim.animateTo(
                                    targetValue = -12f,
                                    animationSpec = tween(80)
                                )
                            }

                            shakeAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(80)
                            )
                        }
                    }

                    is PinResult.LOCKED -> {

                        error = "🔒 Too many attempts! Locked for ${result.minutesLeft} min"
                        pin = ""
                    }

                    else -> {}
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        // Hint + Forgot PIN
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { showHint = !showHint }) {
                Icon(Icons.Default.LightMode, null,
                    tint = Gold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("PIN Hint", color = Gold, fontSize = 13.sp)
            }
            TextButton(onClick = { showRecovery = true }) {
                Icon(Icons.Default.Help, null,
                    tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Forgot PIN?", color = TextMuted, fontSize = 13.sp)
            }
        }

        // Hint display
        AnimatedVisibility(visible = showHint) {
            val hint = VaultPinManager.getHint(context)
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldAlpha)
                    .border(1.dp, Gold.copy(0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 18.sp)
                    Column {
                        Text("Your hint:", fontSize = 11.sp, color = TextMuted)
                        Text(hint.ifBlank { "No hint set" },
                            fontSize = 14.sp, color = Gold,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Recovery dialog
    if (showRecovery) {
        RecoveryDialog(
            onDismiss = { showRecovery = false },
            onRecovered = {
                showRecovery = false
                onUnlocked()
            }
        )
    }
}

// ── Recovery dialog ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryDialog(onDismiss: () -> Unit, onRecovered: () -> Unit) {
    val context   = LocalContext.current
    var answer    by remember { mutableStateOf("") }
    var newPin    by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step      by remember { mutableIntStateOf(1) }
    var error     by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        title = {
            Text(if (step == 1) "🔑 Forgot PIN?" else "🔐 Set New PIN",
                color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (step) {
                    1 -> {
                        Text("❓ What is your mother's first name?",
                            fontSize = 14.sp, color = Gold)
                        OutlinedTextField(
                            value         = answer,
                            onValueChange = { answer = it },
                            placeholder   = { Text("Mother's first name", color = TextMuted) },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            colors        = vaultTextFieldColors()
                        )
                        if (error.isNotBlank())
                            Text(error, color = RedPill, fontSize = 12.sp)
                    }
                    2 -> {
                        Text("Enter new 6-digit PIN",
                            fontSize = 13.sp, color = TextMuted)
                        OutlinedTextField(
                            value         = newPin,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) newPin = it },
                            label         = { Text("New PIN", color = TextMuted) },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            colors        = vaultTextFieldColors()
                        )
                        OutlinedTextField(
                            value         = confirmPin,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) confirmPin = it },
                            label         = { Text("Confirm PIN", color = TextMuted) },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            colors        = vaultTextFieldColors()
                        )
                        if (error.isNotBlank())
                            Text(error, color = RedPill, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (step) {
                        1 -> {
                            if (VaultPinManager.verifyRecovery(context, answer)) {
                                step = 2; error = ""
                            } else {
                                error = "Wrong answer! Try again."
                            }
                        }
                        2 -> {
                            if (newPin.length < 6) { error = "PIN must be 6 digits"; return@Button }
                            if (newPin != confirmPin) { error = "PINs don't match"; return@Button }
                            VaultPinManager.resetPin(context, newPin)
                            onRecovered()
                        }
                    }
                },
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(if (step == 1) "Verify" else "Reset PIN",
                    color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

// ── PIN dots display ──────────────────────────────────────────────────────────
@Composable
fun PinDots(pin: String, maxLen: Int = 6) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        (0 until maxLen).forEach { i ->
            Box(
                Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        if (i < pin.length) Accent else BorderDark
                    )
            )
        }
    }
}

// ── PIN keypad ────────────────────────────────────────────────────────────────
@Composable
fun PinKeypad(
    pin: String,
    onPin: (String) -> Unit,
    maxLen: Int = 6,
    onSubmit: () -> Unit
) {
    val keys = listOf(
        listOf("1","2","3"),
        listOf("4","5","6"),
        listOf("7","8","9"),
        listOf("","0","⌫")
    )

    Column(
        verticalArrangement   = Arrangement.spacedBy(12.dp),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { key ->
                    when {
                        key.isBlank() -> Spacer(Modifier.size(72.dp))
                        key == "⌫"   -> {
                            PinKey(key, color = RedPill) {
                                if (pin.isNotEmpty()) onPin(pin.dropLast(1))
                            }
                        }
                        else -> {
                            PinKey(key) {
                                if (pin.length < maxLen) {
                                    val newPin = pin + key
                                    onPin(newPin)
                                    if (newPin.length == maxLen) onSubmit()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinKey(label: String, color: Color = TextPrimary, onClick: () -> Unit) {
    Box(
        Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Card2Dark)
            .border(1.dp, BorderDark, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize   = if (label == "⌫") 22.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color      = color
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun vaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Accent,
    unfocusedBorderColor    = BorderDark,
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = Accent,
    focusedContainerColor   = Card2Dark,
    unfocusedContainerColor = Card2Dark
)