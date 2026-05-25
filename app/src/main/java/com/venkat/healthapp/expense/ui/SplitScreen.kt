package com.venkat.healthapp.expense.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitExpenseScreen(vm: MainViewModel) {
    val splits by vm.allSplits.collectAsState()
    var showCreate by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(BgDark)) {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Split Expenses", fontSize = 22.sp,
                            fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Split bills among friends",
                            color = TextMuted, fontSize = 13.sp)
                    }
                    FloatingActionButton(
                        onClick        = { showCreate = true },
                        containerColor = Purple,
                        contentColor   = Color.White,
                        shape          = CircleShape,
                        modifier       = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(24.dp))
                    }
                }
            }

            if (splits.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🧾", fontSize = 48.sp)
                            Text("No splits yet", color = TextMuted,
                                fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(
                                "Tap + to split a restaurant bill,\ntrip expense or group outing",
                                color     = TextMuted,
                                fontSize  = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(splits) { split ->
                    SplitCard(split = split, vm = vm)
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showCreate) {
        CreateSplitSheet(
            onDismiss = { showCreate = false },
            onSave    = { split, members ->
                vm.createSplit(split, members)
                showCreate = false
            }
        )
    }
}

// ── Split card ────────────────────────────────────────────────────────────────
@Composable
fun SplitCard(split: SplitExpense, vm: MainViewModel) {
    val context = LocalContext.current
    val members by vm.getMembersForSplit(split.id).collectAsState(initial = emptyList())
    val paidCount = members.count { it.isPaid }
    var expanded  by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp,
                if (split.isSettled) Accent.copy(0.4f) else Purple.copy(0.4f),
                RoundedCornerShape(16.dp))
    ) {
        // Header
        Row(
            Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(46.dp).clip(CircleShape)
                    .background(Purple.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🧾", fontSize = 22.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(split.title, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("${members.size} people  •  ${formatDate(split.date)}",
                    fontSize = 12.sp, color = TextMuted)
                if (split.note.isNotBlank())
                    Text(split.note, fontSize = 11.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹%.2f".format(split.totalAmount),
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Black,
                    color      = Purple)
                Text("$paidCount/${members.size} paid",
                    fontSize = 11.sp,
                    color    = if (paidCount == members.size) Accent else Gold)
                Icon(
                    if (expanded) Icons.Default.ExpandLess
                    else          Icons.Default.ExpandMore,
                    null, tint = TextMuted, modifier = Modifier.size(20.dp)
                )
            }
        }

        // Expanded — member list + send options
        AnimatedVisibility(visible = expanded) {
            Column(
                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = BorderDark)
                Spacer(Modifier.height(4.dp))

                // Members
                members.forEach { member ->
                    MemberRow(
                        member  = member,
                        context = context,
                        onMarkPaid = { vm.markMemberPaid(member) },
                        onSendSms  = {
                            sendSms(context, member.phone,
                                buildSplitMessage(split, member))
                        },
                        onSendWhatsapp = {
                            sendWhatsapp(context, member.phone,
                                buildSplitMessage(split, member))
                        }
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Send to ALL buttons
                Text("Send to everyone:", fontSize = 12.sp, color = TextMuted)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            members.filter { !it.isPaid }.forEach { member ->
                                sendWhatsapp(context, member.phone,
                                    buildSplitMessage(split, member))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366))
                    ) {
                        Text("📱 WhatsApp All",
                            color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            members.filter { !it.isPaid }.forEach { member ->
                                sendSms(context, member.phone,
                                    buildSplitMessage(split, member))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue)
                    ) {
                        Text("✉️ SMS All",
                            color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 12.sp)
                    }
                }

                // Delete
                TextButton(
                    onClick  = { vm.deleteSplit(split) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null,
                        tint = RedPill, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete Split", color = RedPill, fontSize = 12.sp)
                }
            }
        }
    }
}

// ── Member row ────────────────────────────────────────────────────────────────
@Composable
fun MemberRow(
    member: SplitMember,
    context: Context,
    onMarkPaid: () -> Unit,
    onSendSms: () -> Unit,
    onSendWhatsapp: () -> Unit
) {
    val initials = member.name.take(2).uppercase()

    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (member.isPaid) AccentAlpha else Card2Dark)
            .border(
                1.dp,
                if (member.isPaid) Accent.copy(0.4f) else BorderDark,
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape)
                .background(Purple.copy(0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, color = Purple)
        }

        Column(Modifier.weight(1f)) {
            Text(member.name, fontSize = 13.sp,
                fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(
                if (member.phone.isNotBlank()) member.phone else "No phone",
                fontSize = 11.sp, color = TextMuted
            )
        }

        Text("₹%.2f".format(member.shareAmount),
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = if (member.isPaid) Accent else Purple)

        // Action buttons
        if (!member.isPaid) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // WhatsApp
                if (member.phone.isNotBlank()) {
                    IconButton(onClick = onSendWhatsapp,
                        modifier = Modifier.size(30.dp)) {
                        Text("📱", fontSize = 16.sp)
                    }
                    IconButton(onClick = onSendSms,
                        modifier = Modifier.size(30.dp)) {
                        Text("✉️", fontSize = 16.sp)
                    }
                }
                // Mark paid
                IconButton(onClick = onMarkPaid,
                    modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.CheckCircle, null,
                        tint = Accent, modifier = Modifier.size(20.dp))
                }
            }
        } else {
            Text("✅ Paid", fontSize = 11.sp, color = Accent)
        }
    }
}

// ── Create split bottom sheet ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSplitSheet(
    onDismiss: () -> Unit,
    onSave: (SplitExpense, List<SplitMember>) -> Unit
) {
    val context  = LocalContext.current
    var title    by remember { mutableStateOf("") }
    var total    by remember { mutableStateOf("") }
    var note     by remember { mutableStateOf("") }
    var splitMode by remember { mutableStateOf("equal") }   // equal or manual
    var members  by remember { mutableStateOf<List<MutableMember>>(emptyList()) }

    // Contact picker launcher
    val contactLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            val contact = getContactFromUri(context, it)
            contact?.let { c ->
                members = members + MutableMember(c.name, c.phone)
            }
        }
    }

    // Calculate shares
    val totalAmt = total.toFloatOrNull() ?: 0f
    val equalShare = if (members.isNotEmpty() && totalAmt > 0)
        totalAmt / members.size else 0f

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
            Text("🧾 Split Expense",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)

            // Title
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                label         = { Text("What is this for? *", color = TextMuted) },
                placeholder   = {
                    Text("e.g. Restaurant dinner, Trip hotel, Petrol",
                        color = TextMuted)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            // Total amount
            OutlinedTextField(
                value         = total,
                onValueChange = { total = it },
                label         = { Text("Total Amount (₹) *", color = TextMuted) },
                leadingIcon   = {
                    Text("₹", fontSize = 16.sp, color = Purple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp))
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors        = expenseTextFieldColors()
            )

            // Note
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                label         = { Text("Note (optional)", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            // Split mode
            Text("Split Mode", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("equal" to "Equal Split ➗",
                       "manual" to "Manual Split ✏️").forEach { (mode, label) ->
                    val sel = splitMode == mode
                    Box(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) PurpleAlpha else Card2Dark)
                            .border(1.dp,
                                if (sel) Purple else BorderDark,
                                RoundedCornerShape(10.dp))
                            .clickable { splitMode = mode }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 13.sp,
                            color      = if (sel) Purple else TextMuted,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                            textAlign  = TextAlign.Center)
                    }
                }
            }

            // Members section
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("People (${members.size})",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Pick from contacts
                    OutlinedButton(
                        onClick  = { contactLauncher.launch(null) },
                        shape    = RoundedCornerShape(10.dp),
                        border   = BorderStroke(1.dp, Purple.copy(0.5f))
                    ) {
                        Icon(Icons.Default.Contacts, null,
                            tint = Purple, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Contacts", color = Purple, fontSize = 12.sp)
                    }
                    // Add manual
                    OutlinedButton(
                        onClick  = { members = members + MutableMember("", "") },
                        shape    = RoundedCornerShape(10.dp),
                        border   = BorderStroke(1.dp, BorderDark)
                    ) {
                        Icon(Icons.Default.PersonAdd, null,
                            tint = TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Manual", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }

            // Member list
            members.forEachIndexed { i, member ->
                MemberInputRow(
                    member     = member,
                    shareAmount = if (splitMode == "equal") equalShare else null,
                    onNameChange  = { members = members.toMutableList().also { l -> l[i] = l[i].copy(name = it) } },
                    onPhoneChange = { members = members.toMutableList().also { l -> l[i] = l[i].copy(phone = it) } },
                    onShareChange = { members = members.toMutableList().also { l -> l[i] = l[i].copy(manualShare = it) } },
                    onRemove   = { members = members.toMutableList().also { l -> l.removeAt(i) } }
                )
            }

            // Summary
            if (members.isNotEmpty() && totalAmt > 0) {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleAlpha)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Split Summary",
                            fontSize = 13.sp, color = Purple,
                            fontWeight = FontWeight.SemiBold)
                        Text("Total: ₹%.2f".format(totalAmt),
                            fontSize = 13.sp, color = TextPrimary)
                        Text(
                            "Each person: ₹%.2f".format(equalShare),
                            fontSize = 13.sp, color = TextPrimary
                        )
                        Text("People: ${members.size}",
                            fontSize = 13.sp, color = TextPrimary)
                    }
                }
            }

            // Save button
            Button(
                onClick = {
                    val amt = total.toFloatOrNull() ?: return@Button
                    if (title.isBlank() || members.isEmpty()) return@Button

                    val split = SplitExpense(
                        title       = title.trim(),
                        totalAmount = amt,
                        date        = currentDate(),
                        note        = note.trim()
                    )
                    val splitMembers = members.mapIndexed { i, m ->
                        SplitMember(
                            splitExpenseId = 0,
                            name           = m.name.ifBlank { "Person ${i + 1}" },
                            phone          = m.phone,
                            shareAmount    = if (splitMode == "equal") equalShare
                                            else m.manualShare ?: (amt / members.size)
                        )
                    }
                    onSave(split, splitMembers)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                enabled  = title.isNotBlank() && total.isNotBlank() && members.isNotEmpty(),
                colors   = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Icon(Icons.Default.CallSplit, null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create Split",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

data class MutableMember(
    val name: String,
    val phone: String,
    val manualShare: Float? = null
)

@Composable
fun MemberInputRow(
    member: MutableMember,
    shareAmount: Float?,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onShareChange: (Float) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value         = member.name,
                onValueChange = onNameChange,
                placeholder   = { Text("Name", color = TextMuted, fontSize = 12.sp) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(8.dp),
                singleLine    = true,
                colors        = expenseTextFieldColors()
            )
            OutlinedTextField(
                value         = member.phone,
                onValueChange = onPhoneChange,
                placeholder   = { Text("Phone", color = TextMuted, fontSize = 12.sp) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(8.dp),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors        = expenseTextFieldColors()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (shareAmount != null) {
                Text("₹%.0f".format(shareAmount),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Purple)
                Text("each", fontSize = 10.sp, color = TextMuted)
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, null,
                    tint = RedPill, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── Build split message ───────────────────────────────────────────────────────
fun buildSplitMessage(split: SplitExpense, member: SplitMember): String {
    return """
Hi ${member.name}! 👋

We split the expense for "${split.title}" on ${formatDate(split.date)}.

💰 Total amount: ₹%.2f
👥 Your share: ₹%.2f

Please pay me when you get a chance! 🙏

Thank you!
""".trimIndent().format(split.totalAmount, member.shareAmount)
}

// ── Build reminder message for lend/borrow ────────────────────────────────────
fun buildReminderMessage(entry: LendBorrow): String {
    val remaining = entry.amount - entry.paidBack
    return if (entry.type == MoneyType.LENT.name) {
        """
Hi ${entry.personName}! 👋

Just a friendly reminder — you borrowed ₹%.2f from me${if (entry.reason.isNotBlank()) " for ${entry.reason}" else ""} on ${formatDate(entry.date)}.

💰 Remaining amount: ₹%.2f

No rush, just wanted to remind you! 😊

Thanks!
""".trimIndent().format(entry.amount, remaining)
    } else {
        """
Hi ${entry.personName}! 👋

Reminder to myself — I owe you ₹%.2f${if (entry.reason.isNotBlank()) " for ${entry.reason}" else ""}.

💰 Remaining: ₹%.2f

I'll pay you soon! 🙏
""".trimIndent().format(entry.amount, remaining)
    }
}

// ── Send via WhatsApp ─────────────────────────────────────────────────────────
fun sendWhatsapp(context: Context, phone: String, message: String) {
    try {
        val cleanPhone = phone.replace("[^0-9+]".toRegex(), "")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://wa.me/$cleanPhone?text=" +
                Uri.encode(message)
            )
            setPackage("com.whatsapp")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // WhatsApp not installed — fall back to share
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, "Send via"))
    }
}

// ── Send via SMS ──────────────────────────────────────────────────────────────
fun sendSms(context: Context, phone: String, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phone")
        putExtra("sms_body", message)
    }
    context.startActivity(intent)
}

// ── Read contact from URI ─────────────────────────────────────────────────────
fun getContactFromUri(context: Context, uri: Uri): Contact? {
    return try {
        var name  = ""
        var phone = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIdx   = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                name = cursor.getString(nameIdx) ?: ""
                val id = cursor.getString(idIdx)

                // Get phone number
                context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )?.use { phoneCursor ->
                    if (phoneCursor.moveToFirst()) {
                        val phoneIdx = phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phone = phoneCursor.getString(phoneIdx) ?: ""
                    }
                }
            }
        }
        if (name.isNotBlank()) Contact(name, phone) else null
    } catch (e: Exception) { null }
}