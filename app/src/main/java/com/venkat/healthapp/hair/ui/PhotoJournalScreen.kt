package com.venkat.healthapp.hair.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.venkat.healthapp.hair.photo.*
import com.venkat.healthapp.common.*
import com.venkat.healthapp.hair.data.PhotoStorage
import com.venkat.healthapp.hair.data.ScalpPhoto
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoJournalScreen(vm: PhotoViewModel) {
    val context       = LocalContext.current
    val photosByWeek  by vm.photosByWeek.collectAsState()
    val allPhotos     by vm.allPhotos.collectAsState()
    val latestPhoto   by vm.latestPhoto.collectAsState()

    // ── Capture state ─────────────────────────────────────────────────────────
    var showCaptureSheet by remember { mutableStateOf(false) }
    var captureStep      by remember { mutableIntStateOf(0) }  // 0=idle 1=photo1 2=photo2 3=label
    var uri1             by remember { mutableStateOf<Uri?>(null) }
    var uri2             by remember { mutableStateOf<Uri?>(null) }
    var pendingUri       by remember { mutableStateOf<Uri?>(null) }
    var labelText        by remember { mutableStateOf("") }

    // ── Viewer state ──────────────────────────────────────────────────────────
    var viewerPhoto      by remember { mutableStateOf<ScalpPhoto?>(null) }
    var deleteTarget     by remember { mutableStateOf<ScalpPhoto?>(null) }

    // ── Camera launchers ──────────────────────────────────────────────────────
    val cameraLauncher1 = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.onPhoto1Captured()
            uri1 = vm.capturedUri1
            captureStep = 2  // move to photo 2
        }
    }

    val cameraLauncher2 = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.onPhoto2Captured()
            uri2 = vm.capturedUri2
        }
        captureStep = 3  // move to label regardless (photo2 optional)
    }

    // Launch camera when step changes
    LaunchedEffect(captureStep) {
        when (captureStep) {
            1 -> {
                vm.prepareCapture()
                val u = vm.preparePhotoUri(context, "front")
                pendingUri = u
                cameraLauncher1.launch(u)
            }
            2 -> {
                val u = vm.preparePhotoUri(context, "top")
                pendingUri = u
                cameraLauncher2.launch(u)
            }
        }
    }

    Scaffold(containerColor = BgDark) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(Modifier.height(20.dp)) }

            // ── Header ────────────────────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Photo Journal", style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp))
                        Text("Weekly scalp progress tracker", style = MaterialTheme.typography.bodyMedium)
                    }
                    FloatingActionButton(
                        onClick = { captureStep = 1; showCaptureSheet = false },
                        containerColor = Accent,
                        contentColor = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, "Capture", modifier = Modifier.size(24.dp))
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill("📸", "${allPhotos.size}", "Sessions", Accent, Modifier.weight(1f))
                    StatPill("📅", "${photosByWeek.size}", "Weeks", AccentBlue, Modifier.weight(1f))
                    StatPill("🗓", latestPhoto?.weekLabel ?: "—", "Latest", Gold, Modifier.weight(1f))
                }
            }

            // ── Instructions card ─────────────────────────────────────────────
            if (allPhotos.isEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Column(
                            Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("📷", fontSize = 52.sp)
                            Text(
                                "Start Your Scalp Journal",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Capture 2 photos every Sunday (Derma Roller day) to track your regrowth progress visually.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            listOf(
                                "📸 Photo 1 — Front / Side view",
                                "📸 Photo 2 — Top / Crown view",
                                "🏷 Add a label (optional)",
                                "💾 Auto-saved to Week folder"
                            ).forEach { tip ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(tip, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { captureStep = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Capture Week 1", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ── Week groups ───────────────────────────────────────────────────
            photosByWeek.forEach { (weekNum, photos) ->
                item {
                    WeekGroupHeader(weekNum, photos.first())
                }
                items(photos) { photo ->
                    PhotoCard(
                        photo = photo,
                        onView = { viewerPhoto = photo },
                        onDelete = { deleteTarget = photo }
                    )
                }
                item { Spacer(Modifier.height(4.dp)) }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // ── Capture step sheet ────────────────────────────────────────────────────
    if (captureStep == 3) {
        LabelDialog(
            uri1 = uri1,
            uri2 = uri2,
            label = labelText,
            onLabelChange = { labelText = it },
            onSave = {
                vm.saveEntry(labelText)
                labelText = ""
                uri1 = null; uri2 = null
                captureStep = 0
            },
            onRetakePhoto2 = { captureStep = 2 },
            onCancel = {
                vm.cancelCapture()
                labelText = ""
                uri1 = null; uri2 = null
                captureStep = 0
            }
        )
    }

    // ── Full-screen viewer ────────────────────────────────────────────────────
    viewerPhoto?.let { photo ->
        PhotoViewerDialog(photo = photo, onDismiss = { viewerPhoto = null })
    }

    // ── Delete confirmation ───────────────────────────────────────────────────
    deleteTarget?.let { photo ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = CardDark,
            title = { Text("Delete Entry?", color = TextPrimary) },
            text  = {
                Text(
                    "This will permanently delete ${photo.weekLabel} photos and cannot be undone.",
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.deletePhoto(photo); deleteTarget = null }) {
                    Text("Delete", color = RedPill, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

// ── Stat pill ─────────────────────────────────────────────────────────────────
@Composable
fun StatPill(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Card(
        modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold))
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp))
        }
    }
}

// ── Week group header ─────────────────────────────────────────────────────────
@Composable
fun WeekGroupHeader(weekNumber: Int, firstPhoto: ScalpPhoto) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Card2Dark, RoundedCornerShape(12.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(14.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentAlpha),
            contentAlignment = Alignment.Center
        ) {
            Text("W$weekNumber", style = MaterialTheme.typography.labelSmall.copy(color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp))
        }
        Column(Modifier.weight(1f)) {
            Text("Week $weekNumber", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp))
            Text(PhotoStorage.formatDate(firstPhoto.date), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp))
        }
        Text(
            PhotoStorage.formatDateTime(firstPhoto.capturedAt).substringAfter(", "),
            style = MaterialTheme.typography.bodyMedium.copy(color = Accent, fontSize = 11.sp)
        )
    }
}

// ── Photo card ────────────────────────────────────────────────────────────────
@Composable
fun PhotoCard(photo: ScalpPhoto, onView: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onView() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Date & time row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, null, tint = Accent, modifier = Modifier.size(14.dp))
                    Text(
                        PhotoStorage.formatDateTime(photo.capturedAt),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Accent, fontSize = 12.sp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, null, tint = RedPill, modifier = Modifier.size(18.dp))
                }
            }

            // Photos row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PhotoThumb(path = photo.photoPath1, label = "Front / Side", modifier = Modifier.weight(1f))
                if (photo.photoPath2.isNotBlank()) {
                    PhotoThumb(path = photo.photoPath2, label = "Top / Crown", modifier = Modifier.weight(1f))
                } else {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Card2Dark)
                            .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.HideImage, null, tint = TextMuted, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("No top view", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp))
                        }
                    }
                }
            }

            // Label
            if (photo.label.isNotBlank()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldAlpha)
                        .padding(10.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Label, null, tint = Gold, modifier = Modifier.size(14.dp))
                    Text(photo.label, style = MaterialTheme.typography.bodyMedium.copy(color = Gold, fontSize = 12.sp))
                }
            }

            // Tap hint
            Text(
                "Tap card to view full screen",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun PhotoThumb(path: String, label: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(path))
                .crossfade(true)
                .build(),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Card2Dark)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ── Label dialog (after capturing photos) ─────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelDialog(
    uri1: Uri?, uri2: Uri?,
    label: String, onLabelChange: (String) -> Unit,
    onSave: () -> Unit, onRetakePhoto2: () -> Unit, onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Review & Save", style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp))

                // Preview
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CapturePreview(uri1, "Front / Side", Modifier.weight(1f))
                    CapturePreview(uri2, "Top / Crown", Modifier.weight(1f), onRetake = onRetakePhoto2)
                }

                // Label input
                OutlinedTextField(
                    value = label,
                    onValueChange = onLabelChange,
                    label = { Text("Add a note (optional)", color = TextMuted) },
                    placeholder = { Text("e.g. After Derma Roller session", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Accent
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Label, null, tint = Gold, modifier = Modifier.size(18.dp))
                    },
                    maxLines = 2
                )

                // Date/time info
                val now = PhotoStorage.formatDateTime(System.currentTimeMillis())
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Schedule, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text("Will be saved as: $now", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp))
                }

                // Buttons
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uri1 != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Icon(Icons.Default.Save, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CapturePreview(uri: Uri?, label: String, modifier: Modifier, onRetake: (() -> Unit)? = null) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Card2Dark)
        ) {
            if (uri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(uri).crossfade(true).build(),
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Retake overlay
                if (onRetake != null) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                    IconButton(
                        onClick = onRetake,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(Icons.Default.Refresh, "Retake", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = TextMuted, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (onRetake != null) "Skipped" else "Required",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                    )
                }
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ── Full screen viewer ────────────────────────────────────────────────────────
@Composable
fun PhotoViewerDialog(photo: ScalpPhoto, onDismiss: () -> Unit) {
    var selectedPhoto by remember { mutableIntStateOf(0) } // 0=photo1, 1=photo2

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Main image
            val displayPath = if (selectedPhoto == 0 || photo.photoPath2.isBlank())
                photo.photoPath1 else photo.photoPath2

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(displayPath))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Top bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(0.5f))
                    .padding(16.dp, 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(photo.weekLabel, style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontSize = 16.sp))
                    Text(
                        PhotoStorage.formatDateTime(photo.capturedAt),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(0.7f), fontSize = 12.sp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            // Photo selector tabs (only if both photos exist)
            if (photo.photoPath2.isNotBlank()) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.Black.copy(0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Front / Side", "Top / Crown").forEachIndexed { idx, lbl ->
                        val sel = selectedPhoto == idx
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (sel) Accent else Color.Transparent)
                                .clickable { selectedPhoto = idx }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                lbl,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (sel) Color.Black else Color.White,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            // Label chip at bottom
            if (photo.label.isNotBlank()) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(GoldAlpha)
                        .border(1.dp, Gold.copy(0.4f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Label, null, tint = Gold, modifier = Modifier.size(14.dp))
                    Text(photo.label, style = MaterialTheme.typography.bodyMedium.copy(color = Gold, fontSize = 13.sp))
                }
            }
        }
    }
}
