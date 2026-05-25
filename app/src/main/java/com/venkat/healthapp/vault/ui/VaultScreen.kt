package com.venkat.healthapp.vault.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.vault.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(vm: MainViewModel) {
    var isUnlocked   by remember { mutableStateOf(false) }
    var autoLockTime by remember { mutableStateOf(0L) }

    // Auto-lock after 2 minutes of inactivity
    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            autoLockTime = System.currentTimeMillis() + 2 * 60 * 1000L
        }
    }

    if (!isUnlocked) {
        VaultEntryPoint(onUnlocked = { isUnlocked = true })
    } else {
        VaultMainScreen(
            vm       = vm,
            onLock   = { isUnlocked = false }
        )
    }
}

// ── Main vault screen after unlock ───────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultMainScreen(vm: MainViewModel, onLock: () -> Unit) {
    val allItems   by vm.vaultItems.collectAsState()
    val favorites  by vm.vaultFavorites.collectAsState()
    var searchQ    by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<String?>(null) }
    var showAdd    by remember { mutableStateOf(false) }
    var editItem   by remember { mutableStateOf<VaultItem?>(null) }
    var viewItem   by remember { mutableStateOf<VaultItem?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val searchResults by vm.vaultSearch(searchQ).collectAsState(initial = emptyList())

    val displayItems = when {
        searchQ.isNotBlank()     -> searchResults
        selectedCat != null      -> allItems.filter { it.category == selectedCat }
        else                     -> allItems
    }

    Column(Modifier.fillMaxSize().background(BgDark)) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Box(
            Modifier.fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0D1A2E), Color(0xFF0D1117))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("🔐 Secure Vault",
                        fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("${allItems.size} saved credentials",
                        fontSize = 12.sp, color = TextMuted)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, null, tint = TextMuted)
                    }
                    IconButton(onClick = onLock) {
                        Icon(Icons.Default.Lock, null, tint = Accent)
                    }
                }
            }
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(12.dp)) }

            // ── Search bar ────────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value         = searchQ,
                    onValueChange = { searchQ = it },
                    placeholder   = { Text("Search credentials...", color = TextMuted) },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null, tint = Accent)
                    },
                    trailingIcon  = if (searchQ.isNotBlank()) {{
                        IconButton(onClick = { searchQ = "" }) {
                            Icon(Icons.Default.Close, null, tint = TextMuted)
                        }
                    }} else null,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp),
                    colors        = vaultTextFieldColors()
                )
            }

            // ── Category filter ───────────────────────────────────────────────
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CategoryChip(
                            emoji    = "⭐",
                            label    = "All",
                            selected = selectedCat == null,
                            onClick  = { selectedCat = null }
                        )
                    }
                    items(VaultCategory.values()) { cat ->
                        CategoryChip(
                            emoji    = cat.emoji,
                            label    = cat.label.split(" ").first(),
                            selected = selectedCat == cat.name,
                            count    = allItems.count { it.category == cat.name },
                            onClick  = {
                                selectedCat = if (selectedCat == cat.name) null else cat.name
                            }
                        )
                    }
                }
            }

            // ── Favorites ─────────────────────────────────────────────────────
            if (favorites.isNotEmpty() && searchQ.isBlank() && selectedCat == null) {
                item {
                    Text("⭐ Favorites",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Gold)
                }
                items(favorites) { item ->
                    VaultItemCard(
                        item     = item,
                        onView   = { viewItem = item },
                        onEdit   = { editItem = item },
                        onDelete = { vm.deleteVaultItem(item) },
                        onToggleFav = { vm.toggleVaultFavorite(item) }
                    )
                }
                item {
                    Text("All Credentials",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }

            // ── Main list ─────────────────────────────────────────────────────
            if (displayItems.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔐", fontSize = 48.sp)
                            Text(
                                if (searchQ.isNotBlank()) "No results found"
                                else "No credentials saved yet",
                                color = TextMuted, fontSize = 15.sp
                            )
                            Text("Tap + to add your first credential",
                                color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(displayItems) { item ->
                    VaultItemCard(
                        item        = item,
                        onView      = { viewItem = item },
                        onEdit      = { editItem = item },
                        onDelete    = { vm.deleteVaultItem(item) },
                        onToggleFav = { vm.toggleVaultFavorite(item) }
                    )
                }
            }

            // ── Add button ────────────────────────────────────────────────────
            item {
                Button(
                    onClick  = { showAdd = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.Add, null,
                        tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Credential",
                        color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    // ── Sheets ────────────────────────────────────────────────────────────────
    if (showAdd) {
        AddVaultItemSheet(
            onDismiss = { showAdd = false },
            onSave    = { vm.addVaultItem(it); showAdd = false }
        )
    }

    editItem?.let { item ->
        AddVaultItemSheet(
            existing  = item,
            onDismiss = { editItem = null },
            onSave    = { vm.updateVaultItem(it); editItem = null }
        )
    }

    viewItem?.let { item ->
        ViewVaultItemSheet(
            item      = item,
            onDismiss = { viewItem = null },
            onEdit    = { editItem = item; viewItem = null }
        )
    }

    if (showSettings) {
        VaultSettingsSheet(onDismiss = { showSettings = false })
    }
}

// ── Vault item card ───────────────────────────────────────────────────────────
@Composable
fun VaultItemCard(
    item: VaultItem,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFav: () -> Unit
) {
    val cat       = VaultCategory.values().find { it.name == item.category }
        ?: VaultCategory.OTHER
    val catColor  = vaultCategoryColor(item.category)

    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp,
                if (item.isFavorite) Gold.copy(0.4f) else BorderDark,
                RoundedCornerShape(14.dp))
            .clickable { onView() }
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category icon
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
                    .background(catColor.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(cat.emoji, fontSize = 22.sp)
            }

            Column(Modifier.weight(1f)) {
                Text(item.title,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary)
                Text(cat.label, fontSize = 11.sp, color = catColor)
                if (item.username.isNotBlank()) {
                    Text(item.username,
                        fontSize = 12.sp, color = TextMuted,
                        maxLines = 1)
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onToggleFav,
                    modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        null,
                        tint     = if (item.isFavorite) Gold else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete,
                    modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, null,
                        tint = RedPill, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ── View vault item sheet ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewVaultItemSheet(
    item: VaultItem,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var showPassword by remember { mutableStateOf(false) }
    var showExtra    by remember { mutableStateOf(false) }
    var copied       by remember { mutableStateOf("") }

    val decryptedPass  = remember(item) { VaultEncryption.decrypt(item.encryptedPassword) }
    val decryptedExtra = remember(item) { VaultEncryption.decrypt(item.encryptedExtra) }
    val cat = VaultCategory.values().find { it.name == item.category } ?: VaultCategory.OTHER

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                        .background(vaultCategoryColor(item.category).copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) { Text(cat.emoji, fontSize = 26.sp) }
                Column(Modifier.weight(1f)) {
                    Text(item.title, fontSize = 20.sp,
                        fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(cat.label, fontSize = 13.sp,
                        color = vaultCategoryColor(item.category))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = Accent)
                }
            }

            HorizontalDivider(color = BorderDark)

            // Username / Account number
            if (item.username.isNotBlank()) {
                VaultField(
                    label    = "Username / Account No",
                    value    = item.username,
                    onCopy   = {
                        clipboard.setText(AnnotatedString(item.username))
                        copied = "Username"
                    }
                )
            }

            // Password / PIN
            if (decryptedPass.isNotBlank()) {
                VaultSecretField(
                    label      = "Password / PIN",
                    value      = decryptedPass,
                    isVisible  = showPassword,
                    onToggle   = { showPassword = !showPassword },
                    onCopy     = {
                        clipboard.setText(AnnotatedString(decryptedPass))
                        copied = "Password"
                    }
                )
            }

            // Extra (Customer ID, IFSC etc)
            if (decryptedExtra.isNotBlank()) {
                VaultSecretField(
                    label      = "Extra Info (Customer ID / IFSC / PIN)",
                    value      = decryptedExtra,
                    isVisible  = showExtra,
                    onToggle   = { showExtra = !showExtra },
                    onCopy     = {
                        clipboard.setText(AnnotatedString(decryptedExtra))
                        copied = "Extra info"
                    }
                )
            }

            // Website
            if (item.website.isNotBlank()) {
                VaultField(label = "Website / URL", value = item.website, onCopy = {
                    clipboard.setText(AnnotatedString(item.website))
                    copied = "URL"
                })
            }

            // Note
            if (item.note.isNotBlank()) {
                Column(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Card2Dark)
                        .padding(14.dp)
                ) {
                    Text("Note", fontSize = 11.sp, color = TextMuted)
                    Spacer(Modifier.height(4.dp))
                    Text(item.note, fontSize = 14.sp, color = TextPrimary)
                }
            }

            // Copy confirmation
            AnimatedVisibility(visible = copied.isNotBlank()) {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentAlpha)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✅ $copied copied to clipboard!",
                        color = Accent, fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold)
                }
            }

            // Last updated
            Text(
                "Last updated: ${
                    java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a",
                        java.util.Locale.getDefault()).format(java.util.Date(item.updatedAt))
                }",
                fontSize = 11.sp, color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Vault field components ────────────────────────────────────────────────────
@Composable
fun VaultField(label: String, value: String, onCopy: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = TextMuted)
                Spacer(Modifier.height(3.dp))
                Text(value, fontSize = 15.sp, color = TextPrimary,
                    fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, null,
                    tint = Accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun VaultSecretField(
    label: String,
    value: String,
    isVisible: Boolean,
    onToggle: () -> Unit,
    onCopy: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card2Dark)
            .border(1.dp, Accent.copy(0.3f), RoundedCornerShape(10.dp))
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = TextMuted)
                Spacer(Modifier.height(3.dp))
                Text(
                    if (isVisible) value else "•".repeat(value.length.coerceAtMost(12)),
                    fontSize   = if (isVisible) 15.sp else 20.sp,
                    color      = if (isVisible) TextPrimary else TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    if (isVisible) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    null,
                    tint     = Gold,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, null,
                    tint = Accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── Add / Edit vault item sheet ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaultItemSheet(
    existing: VaultItem? = null,
    onDismiss: () -> Unit,
    onSave: (VaultItem) -> Unit
) {
    var category  by remember { mutableStateOf(existing?.category ?: VaultCategory.BANK.name) }
    var title     by remember { mutableStateOf(existing?.title ?: "") }
    var username  by remember { mutableStateOf(existing?.username ?: "") }
    var password  by remember { mutableStateOf(
        if (existing != null) VaultEncryption.decrypt(existing.encryptedPassword) else "") }
    var extra     by remember { mutableStateOf(
        if (existing != null) VaultEncryption.decrypt(existing.encryptedExtra) else "") }
    var note      by remember { mutableStateOf(existing?.note ?: "") }
    var website   by remember { mutableStateOf(existing?.website ?: "") }
    var showPass  by remember { mutableStateOf(false) }
    var showExtra by remember { mutableStateOf(false) }

    val cat = VaultCategory.values().find { it.name == category } ?: VaultCategory.BANK

    // Dynamic field labels based on category
    val (userLabel, passLabel, extraLabel) = when (cat) {
        VaultCategory.BANK       -> Triple("Account Number", "Net Banking Password / ATM PIN", "Customer ID / IFSC Code")
        VaultCategory.CARD       -> Triple("Card Number", "CVV", "PIN / Expiry Date")
        VaultCategory.UPI        -> Triple("UPI ID / Phone", "MPIN / PIN", "Bank linked")
        VaultCategory.EMAIL      -> Triple("Email Address", "Password", "Recovery Phone")
        VaultCategory.SOCIAL     -> Triple("Username / Phone", "Password", "Backup Code")
        VaultCategory.WIFI       -> Triple("WiFi Name (SSID)", "Password", "Router IP")
        VaultCategory.SYSTEM     -> Triple("Username / Device ID", "Password / PIN", "License Key")
        VaultCategory.GOVERNMENT -> Triple("ID Number", "Password (if any)", "DOB / Linked Mobile")
        VaultCategory.INSURANCE  -> Triple("Policy Number", "Password", "Customer ID")
        else                     -> Triple("Username / ID", "Password / PIN", "Extra Info")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                if (existing != null) "Edit Credential" else "Add Credential",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary
            )

            // Category selector
            Text("Category", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(VaultCategory.values()) { c ->
                    val sel = category == c.name
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp))
                            .background(
                                if (sel) vaultCategoryColor(c.name).copy(0.2f) else Card2Dark
                            )
                            .border(1.dp,
                                if (sel) vaultCategoryColor(c.name) else BorderDark,
                                RoundedCornerShape(10.dp))
                            .clickable { category = c.name }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(c.emoji, fontSize = 18.sp)
                            Text(
                                c.label.split(" ").first(),
                                fontSize   = 10.sp,
                                color      = if (sel) vaultCategoryColor(c.name) else TextMuted,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Title
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                label         = { Text("Title *  (e.g. SBI Savings Account)", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            // Username / Account
            OutlinedTextField(
                value         = username,
                onValueChange = { username = it },
                label         = { Text(userLabel, color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            // Password — with show/hide
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                label         = { Text(passLabel, color = TextMuted) },
                trailingIcon  = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null, tint = Gold
                        )
                    }
                },
                visualTransformation = if (showPass)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            // Extra
            OutlinedTextField(
                value         = extra,
                onValueChange = { extra = it },
                label         = { Text(extraLabel, color = TextMuted) },
                trailingIcon  = {
                    IconButton(onClick = { showExtra = !showExtra }) {
                        Icon(
                            if (showExtra) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null, tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (showExtra)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            // Website
            OutlinedTextField(
                value         = website,
                onValueChange = { website = it },
                label         = { Text("Website / App URL (optional)", color = TextMuted) },
                leadingIcon   = { Icon(Icons.Default.Language, null, tint = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            // Note
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                label         = { Text("Note (optional)", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                maxLines      = 3,
                colors        = vaultTextFieldColors()
            )

            // Security notice
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentAlpha)
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, null,
                        tint = Accent, modifier = Modifier.size(16.dp))
                    Text(
                        "Password and extra fields are encrypted using AES-256 with Android Keystore. Only readable on this device.",
                        fontSize = 11.sp, color = Accent
                    )
                }
            }

            // Save
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val item = (existing ?: VaultItem(
                        category  = category,
                        title     = title.trim(),
                        username  = username.trim(),
                        encryptedPassword = VaultEncryption.encrypt(password),
                        encryptedExtra    = VaultEncryption.encrypt(extra),
                        note      = note.trim(),
                        website   = website.trim()
                    )).let {
                        if (existing != null) it.copy(
                            category  = category,
                            title     = title.trim(),
                            username  = username.trim(),
                            encryptedPassword = VaultEncryption.encrypt(password),
                            encryptedExtra    = VaultEncryption.encrypt(extra),
                            note      = note.trim(),
                            website   = website.trim(),
                            updatedAt = System.currentTimeMillis()
                        ) else it
                    }
                    onSave(item)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                enabled  = title.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Icon(Icons.Default.Lock, null,
                    tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (existing != null) "Update Credential" else "Save Encrypted",
                    color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Settings sheet — change PIN ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultSettingsSheet(onDismiss: () -> Unit) {
    val context  = LocalContext.current
    var oldPin   by remember { mutableStateOf("") }
    var newPin   by remember { mutableStateOf("") }
    var newHint  by remember { mutableStateOf("") }
    var message  by remember { mutableStateOf("") }
    var success  by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("⚙️ Vault Settings",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)

            Text("Change PIN",
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

            OutlinedTextField(
                value         = oldPin,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) oldPin = it },
                label         = { Text("Current PIN", color = TextMuted) },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )
            OutlinedTextField(
                value         = newPin,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) newPin = it },
                label         = { Text("New PIN (6 digits)", color = TextMuted) },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )
            OutlinedTextField(
                value         = newHint,
                onValueChange = { newHint = it },
                label         = { Text("New PIN Hint", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = vaultTextFieldColors()
            )

            if (message.isNotBlank()) {
                Text(message,
                    color = if (success) Accent else RedPill,
                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = {
                    if (newPin.length < 6) { message = "New PIN must be 6 digits"; return@Button }
                    val changed = VaultPinManager.changePin(context, oldPin, newPin, newHint)
                    if (changed) { success = true; message = "✅ PIN changed successfully!" }
                    else { success = false; message = "❌ Current PIN is wrong!" }
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Change PIN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Category chip ─────────────────────────────────────────────────────────────
@Composable
fun CategoryChip(
    emoji: String, label: String,
    selected: Boolean, count: Int = 0,
    onClick: () -> Unit
) {
    Box(
        Modifier.clip(RoundedCornerShape(100.dp))
            .background(if (selected) AccentAlpha else Card2Dark)
            .border(1.dp, if (selected) Accent else BorderDark, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 14.sp)
            Text(label, fontSize = 12.sp,
                color      = if (selected) Accent else TextMuted,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
            if (count > 0) {
                Text("$count", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

fun vaultCategoryColor(category: String): Color = when (category) {
    "BANK"       -> Accent
    "CARD"       -> Gold
    "UPI"        -> AccentBlue
    "EMAIL"      -> Color(0xFF4CAF50)
    "SOCIAL"     -> Color(0xFFE91E63)
    "APP"        -> Purple
    "WIFI"       -> Color(0xFF00BCD4)
    "SYSTEM"     -> Color(0xFFFF9800)
    "GOVERNMENT" -> Color(0xFF9C27B0)
    "INSURANCE"  -> Color(0xFF607D8B)
    else         -> TextMuted
}