package com.venkat.healthapp.expense.receipt

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.data.Expense
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(vm: ReceiptViewModel) {
    val context    = LocalContext.current
    val receipts   by vm.allReceipts.collectAsState()
    val processing by vm.isProcessing.collectAsState()
    val ocrResult  by vm.ocrResult.collectAsState()

    var pendingUri    by remember { mutableStateOf<android.net.Uri?>(null) }
    var showReview    by remember { mutableStateOf(false) }
    var viewReceipt   by remember { mutableStateOf<Receipt?>(null) }
    var searchQ       by remember { mutableStateOf("") }
    var noteText      by remember { mutableStateOf("") }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.processPhoto()
            showReview = true
        } else {
            vm.cancelCapture()
        }
    }

    Column(Modifier.fillMaxSize().background(BgDark)) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Box(
            Modifier.fillMaxWidth().background(CardDark).padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("📸 Receipt Scanner",
                        fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("${receipts.size} receipts saved",
                        fontSize = 12.sp, color = TextMuted)
                }
                FloatingActionButton(
                    onClick = {
                        val uri = vm.prepareCameraFile(context)
                        pendingUri = uri
                        cameraLauncher.launch(uri)
                    },
                    containerColor = Accent,
                    contentColor   = Color.Black,
                    shape          = CircleShape,
                    modifier       = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(24.dp))
                }
            }
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(12.dp)) }

            // ── Search ────────────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value         = searchQ,
                    onValueChange = { searchQ = it },
                    placeholder   = { Text("Search receipts...", color = TextMuted) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = Accent) },
                    trailingIcon  = if (searchQ.isNotBlank()) {{
                        IconButton(onClick = { searchQ = "" }) {
                            Icon(Icons.Default.Close, null, tint = TextMuted)
                        }
                    }} else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = receiptTextFieldColors()
                )
            }

            // ── Stats row ─────────────────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReceiptStatCard(
                        "📸", "${receipts.size}", "Total\nReceipts",
                        Accent, Modifier.weight(1f)
                    )
                    ReceiptStatCard(
                        "🔍",
                        "${receipts.count { it.detectedAmount > 0 }}",
                        "OCR\nDetected",
                        Gold, Modifier.weight(1f)
                    )
                    ReceiptStatCard(
                        "🔗",
                        "${receipts.count { it.expenseId > 0 }}",
                        "Linked to\nExpense",
                        AccentBlue, Modifier.weight(1f)
                    )
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            if (receipts.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("📸", fontSize = 56.sp)
                            Text("No receipts yet",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary)
                            Text(
                                "Tap the camera button to scan\nyour first receipt",
                                fontSize  = 13.sp,
                                color     = TextMuted,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            listOf(
                                "📷 Capture receipt photo",
                                "🔍 Auto-detect amount via OCR",
                                "🔗 Link to expense entry",
                                "📂 Stored permanently"
                            ).forEach { tip ->
                                Text(tip, fontSize = 13.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // ── Receipt list ──────────────────────────────────────────────────
            val displayReceipts = if (searchQ.isBlank()) receipts
            else receipts.filter {
                it.detectedMerchant.contains(searchQ, ignoreCase = true) ||
                        it.ocrText.contains(searchQ, ignoreCase = true) ||
                        it.note.contains(searchQ, ignoreCase = true)
            }

            items(displayReceipts) { receipt ->
                ReceiptCard(
                    receipt  = receipt,
                    onView   = { viewReceipt = receipt },
                    onDelete = { vm.deleteReceipt(receipt) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // ── OCR Review sheet ──────────────────────────────────────────────────────
    if (showReview) {
        OcrReviewSheet(
            isProcessing = processing,
            ocrResult    = ocrResult,
            noteText     = noteText,
            onNoteChange = { noteText = it },
            onSave = { note ->
                vm.saveReceipt(note = note, ocrResult = ocrResult)
                noteText  = ""
                showReview = false
            },
            onDiscard = {
                vm.cancelCapture()
                noteText  = ""
                showReview = false
            }
        )
    }

    // ── Full screen viewer ────────────────────────────────────────────────────
    viewReceipt?.let { receipt ->
        ReceiptViewerDialog(
            receipt   = receipt,
            onDismiss = { viewReceipt = null },
            onDelete  = { vm.deleteReceipt(receipt); viewReceipt = null }
        )
    }
}

// ── Receipt card ──────────────────────────────────────────────────────────────
@Composable
fun ReceiptCard(receipt: Receipt, onView: () -> Unit, onDelete: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable { onView() }
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(receipt.imagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Receipt",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Card2Dark)
            )

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Merchant name or fallback
                Text(
                    receipt.detectedMerchant.ifBlank { "Receipt" },
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Text(
                    ReceiptStorage.formatDateTime(receipt.capturedAt),
                    fontSize = 11.sp, color = TextMuted
                )
                if (receipt.note.isNotBlank()) {
                    Text(receipt.note,
                        fontSize = 12.sp, color = TextMuted,
                        maxLines = 1)
                }
                // Tags row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (receipt.detectedAmount > 0) {
                        ReceiptTag("₹%.0f".format(receipt.detectedAmount), Accent)
                    }
                    if (receipt.expenseId > 0) {
                        ReceiptTag("🔗 Linked", AccentBlue)
                    }
                    if (receipt.ocrText.isNotBlank()) {
                        ReceiptTag("🔍 OCR", Gold)
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, null,
                    tint = RedPill, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ReceiptTag(text: String, color: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(100.dp))
            .background(color.copy(0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

// ── OCR Review sheet ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrReviewSheet(
    isProcessing: Boolean,
    ocrResult: OcrResult?,
    noteText: String,
    onNoteChange: (String) -> Unit,
    onSave: (String) -> Unit,
    onDiscard: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDiscard,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📸 Receipt Scanned",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)

            // Processing indicator
            if (isProcessing) {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentAlpha)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Accent, modifier = Modifier.size(36.dp))
                        Text("Reading receipt with OCR...",
                            color = Accent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Detecting amount, date and merchant",
                            color = TextMuted, fontSize = 12.sp)
                    }
                }
            }

            // OCR Results
            ocrResult?.let { result ->
                Text("🔍 OCR Detection Results",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                // Detected fields
                Column(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Card2Dark)
                        .border(1.dp, Accent.copy(0.3f), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Amount
                    OcrField(
                        label  = "💰 Detected Amount",
                        value  = if (result.detectedAmount > 0)
                            "₹%.2f".format(result.detectedAmount)
                        else "Not detected",
                        color  = if (result.detectedAmount > 0) Accent else TextMuted,
                        found  = result.detectedAmount > 0
                    )

                    // Merchant
                    OcrField(
                        label  = "🏪 Merchant / Shop",
                        value  = result.detectedMerchant.ifBlank { "Not detected" },
                        color  = if (result.detectedMerchant.isNotBlank()) Gold else TextMuted,
                        found  = result.detectedMerchant.isNotBlank()
                    )

                    // Date
                    OcrField(
                        label  = "📅 Bill Date",
                        value  = result.detectedDate.ifBlank { "Not detected" },
                        color  = if (result.detectedDate.isNotBlank()) AccentBlue else TextMuted,
                        found  = result.detectedDate.isNotBlank()
                    )
                }

                // Raw OCR text (expandable)
                if (result.rawText.isNotBlank()) {
                    var showRaw by remember { mutableStateOf(false) }
                    TextButton(
                        onClick  = { showRaw = !showRaw },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (showRaw) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null, tint = TextMuted, modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (showRaw) "Hide raw text" else "Show all extracted text",
                            color = TextMuted, fontSize = 12.sp
                        )
                    }
                    AnimatedVisibility(visible = showRaw) {
                        Box(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Card2Dark)
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                result.rawText,
                                fontSize = 11.sp,
                                color    = TextMuted,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Note field
            OutlinedTextField(
                value         = noteText,
                onValueChange = onNoteChange,
                label         = { Text("Add a note (optional)", color = TextMuted) },
                placeholder   = { Text("e.g. Pharmacy bill, Restaurant dinner...", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                maxLines      = 3,
                colors        = receiptTextFieldColors()
            )

            // Action buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick  = onDiscard,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp),
                    border   = BorderStroke(1.dp, BorderDark)
                ) {
                    Icon(Icons.Default.Delete, null,
                        tint = RedPill, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Discard", color = RedPill)
                }
                Button(
                    onClick  = { onSave(noteText) },
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp),
                    enabled  = !isProcessing,
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.Save, null,
                        tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Save Receipt",
                        color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── OCR field display ─────────────────────────────────────────────────────────
@Composable
fun OcrField(label: String, value: String, color: Color, found: Boolean) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Text(value, fontSize = 15.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
        Icon(
            if (found) Icons.Default.CheckCircle else Icons.Default.Cancel,
            null,
            tint     = if (found) Accent else TextMuted.copy(0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Full screen viewer ────────────────────────────────────────────────────────
@Composable
fun ReceiptViewerDialog(
    receipt: Receipt,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier.fillMaxSize().background(Color.Black)
        ) {
            // Full image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(receipt.imagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Receipt",
                contentScale       = ContentScale.Fit,
                modifier           = Modifier.fillMaxSize()
            )

            // Top bar
            Row(
                Modifier.fillMaxWidth()
                    .background(Color.Black.copy(0.6f))
                    .padding(16.dp, 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        receipt.detectedMerchant.ifBlank { "Receipt" },
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Text(
                        ReceiptStorage.formatDateTime(receipt.capturedAt),
                        fontSize = 12.sp,
                        color    = Color.White.copy(0.7f)
                    )
                }
                Row {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, null,
                            tint = RedPill, modifier = Modifier.size(22.dp))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }
            }

            // Bottom info
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(0.7f))
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (receipt.detectedAmount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("💰", fontSize = 16.sp)
                        Text("₹%.2f".format(receipt.detectedAmount),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Black,
                            color      = Accent)
                    }
                }
                if (receipt.detectedDate.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("📅", fontSize = 14.sp)
                        Text(receipt.detectedDate,
                            fontSize = 14.sp, color = Color.White.copy(0.8f))
                    }
                }
                if (receipt.note.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("📝", fontSize = 14.sp)
                        Text(receipt.note,
                            fontSize = 13.sp, color = Color.White.copy(0.7f))
                    }
                }
            }
        }
    }

    // Delete confirm
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor   = CardDark,
            title = { Text("Delete Receipt?", color = TextPrimary) },
            text  = { Text("This will permanently delete the receipt image.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors  = ButtonDefaults.buttonColors(containerColor = RedPill),
                    shape   = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────
@Composable
fun ReceiptStatCard(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp)).background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp)).padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextMuted,
                textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun receiptTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Accent,
    unfocusedBorderColor    = BorderDark,
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = Accent,
    focusedContainerColor   = Card2Dark,
    unfocusedContainerColor = Card2Dark
)