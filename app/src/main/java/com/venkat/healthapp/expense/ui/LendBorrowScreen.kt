package com.venkat.healthapp.expense.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendBorrowScreen(vm: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "I Lent", "I Borrowed", "Settled")

    Column(Modifier.fillMaxSize().background(BgDark)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor   = CardDark,
            contentColor     = Accent,
            edgePadding      = 16.dp
        ) {
            tabs.forEachIndexed { i, t ->
                Tab(
                    selected = selectedTab == i,
                    onClick  = { selectedTab = i },
                    text = {
                        Text(
                            t, fontSize = 13.sp,
                            fontWeight = if (selectedTab == i) FontWeight.Bold
                                        else FontWeight.Normal,
                            color = if (selectedTab == i) Accent else TextMuted
                        )
                    }
                )
            }
        }
        when (selectedTab) {
            0 -> LendBorrowOverview(vm)
            1 -> LentListTab(vm)
            2 -> BorrowedListTab(vm)
            3 -> SettledTab(vm)
        }
    }
}

// ── OVERVIEW tab ──────────────────────────────────────────────────────────────
@Composable
fun LendBorrowOverview(vm: MainViewModel) {
    val totalLent     by vm.totalLentPending.collectAsState()
    val totalBorrowed by vm.totalBorrowedPending.collectAsState()
    val pendingLent   by vm.pendingLent.collectAsState()
    val pendingBorr   by vm.pendingBorrowed.collectAsState()

    val netAmount = (totalLent ?: 0f) - (totalBorrowed ?: 0f)

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Text("Money Tracker", fontSize = 22.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Track who owes you and who you owe",
                color = TextMuted, fontSize = 13.sp)
        }

        // ── Net summary card ──────────────────────────────────────────────────
        item {
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            if (netAmount >= 0)
                                listOf(Color(0xFF0D2A1F), Color(0xFF0D1117))
                            else
                                listOf(Color(0xFF2A0D0D), Color(0xFF0D1117))
                        )
                    )
                    .border(
                        1.dp,
                        if (netAmount >= 0) Accent.copy(0.4f) else RedPill.copy(0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Net Balance", fontSize = 13.sp, color = TextMuted)
                    Text(
                        (if (netAmount >= 0) "+ " else "- ") +
                        "₹%.2f".format(kotlin.math.abs(netAmount)),
                        fontSize   = 36.sp,
                        fontWeight = FontWeight.Black,
                        color      = if (netAmount >= 0) Accent else RedPill
                    )
                    Text(
                        if (netAmount > 0) "Friends owe you more than you owe"
                        else if (netAmount < 0) "You owe more than friends owe you"
                        else "All balanced! ✅",
                        fontSize = 13.sp, color = TextMuted
                    )
                }
            }
        }

        // ── Two stat cards ────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Will receive
                Box(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentAlpha)
                        .border(1.dp, Accent.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("📥 Will Receive",
                            fontSize = 12.sp, color = TextMuted)
                        Text(
                            "₹%.2f".format(totalLent ?: 0f),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Black,
                            color      = Accent
                        )
                        Text("${pendingLent.size} friends",
                            fontSize = 11.sp, color = TextMuted)
                    }
                }

                // Will pay
                Box(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(RedAlpha)
                        .border(1.dp, RedPill.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("📤 Will Pay",
                            fontSize = 12.sp, color = TextMuted)
                        Text(
                            "₹%.2f".format(totalBorrowed ?: 0f),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Black,
                            color      = RedPill
                        )
                        Text("${pendingBorr.size} friends",
                            fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }

        // ── Quick pending list ────────────────────────────────────────────────
        if (pendingLent.isNotEmpty()) {
            item {
                Text("People who owe you 📥",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    color = Accent)
            }
            items(pendingLent.take(3)) { entry ->
                MiniDebtCard(entry, Accent)
            }
            if (pendingLent.size > 3) {
                item {
                    Text("+ ${pendingLent.size - 3} more in 'I Lent' tab",
                        fontSize = 12.sp, color = TextMuted,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        if (pendingBorr.isNotEmpty()) {
            item {
                Text("You owe these people 📤",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    color = RedPill)
            }
            items(pendingBorr.take(3)) { entry ->
                MiniDebtCard(entry, RedPill)
            }
            if (pendingBorr.size > 3) {
                item {
                    Text("+ ${pendingBorr.size - 3} more in 'I Borrowed' tab",
                        fontSize = 12.sp, color = TextMuted,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun MiniDebtCard(entry: LendBorrow, color: Color) {
    val remaining = entry.amount - entry.paidBack
    val initials  = entry.personName.take(2).uppercase()

    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape)
                .background(color.copy(0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, fontSize = 14.sp,
                fontWeight = FontWeight.Bold, color = color)
        }
        Column(Modifier.weight(1f)) {
            Text(entry.personName,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = TextPrimary)
            Text(entry.reason.ifBlank { formatDate(entry.date) },
                fontSize = 11.sp, color = TextMuted)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹%.2f".format(remaining),
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = color)
            if (entry.paidBack > 0) {
                Text("₹%.0f paid".format(entry.paidBack),
                    fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

// ── I LENT tab ────────────────────────────────────────────────────────────────
@Composable
fun LentListTab(vm: MainViewModel) {
    val entries     by vm.pendingLent.collectAsState()
    var showAdd     by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<LendBorrow?>(null) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("I Lent Money", fontSize = 18.sp,
                            fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("People who owe you",
                            color = TextMuted, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { showAdd = true },
                        colors  = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape   = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null,
                            tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (entries.isEmpty()) {
                item { EmptyDebtCard("📥", "No pending lent amounts", "Add who owes you money") }
            } else {
                items(entries) { entry ->
                    DebtCard(
                        entry     = entry,
                        color     = Accent,
                        onSettle  = { vm.settleDebt(entry) },
                        onPartial = { amount -> vm.addPartialPayment(entry, amount) },
                        onDelete  = { vm.deleteLendBorrow(entry) },
                        onRemind  = { vm.sendReminder(entry) }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAdd) {
        AddLendBorrowSheet(
            type      = MoneyType.LENT,
            onDismiss = { showAdd = false },
            onSave    = { vm.addLendBorrow(it); showAdd = false }
        )
    }
}

// ── I BORROWED tab ────────────────────────────────────────────────────────────
@Composable
fun BorrowedListTab(vm: MainViewModel) {
    val entries by vm.pendingBorrowed.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("I Borrowed Money", fontSize = 18.sp,
                            fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("People you owe money to",
                            color = TextMuted, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { showAdd = true },
                        colors  = ButtonDefaults.buttonColors(containerColor = RedPill),
                        shape   = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null,
                            tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (entries.isEmpty()) {
                item {
                    EmptyDebtCard("📤", "No pending borrowed amounts",
                        "Add money you borrowed from friends")
                }
            } else {
                items(entries) { entry ->
                    DebtCard(
                        entry     = entry,
                        color     = RedPill,
                        onSettle  = { vm.settleDebt(entry) },
                        onPartial = { amount -> vm.addPartialPayment(entry, amount) },
                        onDelete  = { vm.deleteLendBorrow(entry) },
                        onRemind  = { vm.sendReminder(entry) }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAdd) {
        AddLendBorrowSheet(
            type      = MoneyType.BORROWED,
            onDismiss = { showAdd = false },
            onSave    = { vm.addLendBorrow(it); showAdd = false }
        )
    }
}

// ── SETTLED tab ───────────────────────────────────────────────────────────────
@Composable
fun SettledTab(vm: MainViewModel) {
    val settled by vm.settledDebts.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Text("Settled ✅", fontSize = 18.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text("All cleared transactions", color = TextMuted, fontSize = 12.sp)
        }
        if (settled.isEmpty()) {
            item { EmptyDebtCard("✅", "No settled transactions yet", "") }
        } else {
            items(settled) { entry ->
                val isLent = entry.type == MoneyType.LENT.name
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(14.dp, 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(if (isLent) "✅" else "✅", fontSize = 20.sp)
                    Column(Modifier.weight(1f)) {
                        Text(entry.personName,
                            fontSize = 14.sp, color = TextPrimary,
                            fontWeight = FontWeight.Medium)
                        Text(
                            (if (isLent) "You lent" else "You borrowed") +
                            " • " + formatDate(entry.date),
                            fontSize = 11.sp, color = TextMuted
                        )
                    }
                    Text("₹%.2f".format(entry.amount),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = if (isLent) Accent else RedPill)
                }
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Debt card with partial payment ───────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtCard(
    entry: LendBorrow, color: Color,
    onSettle: () -> Unit,
    onPartial: (Float) -> Unit,
    onDelete: () -> Unit,
    onRemind: () -> Unit
) {
    val remaining  = entry.amount - entry.paidBack
    val pct        = if (entry.amount > 0) entry.paidBack / entry.amount else 0f
    val initials   = entry.personName.take(2).uppercase()
    var expanded   by remember { mutableStateOf(false) }
    var showPartial by remember { mutableStateOf(false) }
    var partialAmt by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, color.copy(0.35f), RoundedCornerShape(16.dp))
    ) {
        // Main row
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                Modifier.size(46.dp).clip(CircleShape).background(color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = color)
            }

            Column(Modifier.weight(1f)) {
                Text(entry.personName, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (entry.reason.isNotBlank()) {
                    Text(entry.reason, fontSize = 12.sp, color = TextMuted)
                }
                Text(formatDate(entry.date), fontSize = 11.sp, color = TextMuted)
                if (entry.dueDate.isNotBlank()) {
                    Text("Due: ${formatDate(entry.dueDate)}",
                        fontSize = 11.sp, color = Gold)
                }

                // Progress bar
                if (entry.paidBack > 0) {
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.fillMaxWidth().height(4.dp)
                        .clip(RoundedCornerShape(100.dp)).background(BorderDark)) {
                        Box(Modifier.fillMaxWidth(pct).height(4.dp)
                            .background(color, RoundedCornerShape(100.dp)))
                    }
                    Text("₹%.0f / ₹%.0f returned".format(entry.paidBack, entry.amount),
                        fontSize = 10.sp, color = TextMuted)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("₹%.2f".format(remaining),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Black,
                    color      = color)
                Text("remaining", fontSize = 10.sp, color = TextMuted)
                IconButton(
                    onClick  = { expanded = !expanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        null, tint = TextMuted
                    )
                }
            }
        }

        // Expanded actions
        AnimatedVisibility(visible = expanded) {
            Column(
                Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = BorderDark)
                Spacer(Modifier.height(4.dp))

                // Action buttons row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mark settled
                    Button(
                        onClick  = onSettle,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = color)
                    ) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Settled", color = Color.Black,
                            fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Partial payment
                    OutlinedButton(
                        onClick  = { showPartial = !showPartial },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        border   = BorderStroke(1.dp, color.copy(0.5f))
                    ) {
                        Text("Partial", color = color, fontSize = 12.sp)
                    }

                    // Remind
                    OutlinedButton(
                        onClick  = onRemind,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        border   = BorderStroke(1.dp, Gold.copy(0.5f))
                    ) {
                        Icon(Icons.Default.Send, null,
                            tint = Gold, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Remind", color = Gold, fontSize = 11.sp)
                    }
                }

                // Delete
                TextButton(
                    onClick  = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null,
                        tint = RedPill, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete Entry", color = RedPill, fontSize = 12.sp)
                }

                // Partial payment input
                AnimatedVisibility(visible = showPartial) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value         = partialAmt,
                            onValueChange = { partialAmt = it },
                            label         = { Text("Amount returned", color = TextMuted) },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal),
                            colors        = expenseTextFieldColors()
                        )
                        Button(
                            onClick = {
                                partialAmt.toFloatOrNull()?.let {
                                    onPartial(it)
                                    partialAmt = ""
                                    showPartial = false
                                }
                            },
                            shape  = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color)
                        ) {
                            Text("Add", color = Color.Black,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ── Add Lend/Borrow bottom sheet ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLendBorrowSheet(
    type: MoneyType,
    onDismiss: () -> Unit,
    onSave: (LendBorrow) -> Unit
) {
    val isLent    = type == MoneyType.LENT
    val color     = if (isLent) Accent else RedPill

    var name      by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var amount    by remember { mutableStateOf("") }
    var reason    by remember { mutableStateOf("") }
    var dueDate   by remember { mutableStateOf("") }

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
                if (isLent) "📥 I Lent Money" else "📤 I Borrowed Money",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = color
            )
            Text(
                if (isLent) "Add someone who owes you"
                else "Add someone you owe",
                color = TextMuted, fontSize = 13.sp
            )

            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                label         = { Text("Friend's Name *", color = TextMuted) },
                leadingIcon   = {
                    Icon(Icons.Default.Person, null, tint = color)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            OutlinedTextField(
                value         = phone,
                onValueChange = { phone = it },
                label         = { Text("Phone Number", color = TextMuted) },
                leadingIcon   = {
                    Icon(Icons.Default.Phone, null, tint = TextMuted)
                },
                placeholder   = {
                    Text("For sending reminders", color = TextMuted)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors        = expenseTextFieldColors()
            )

            OutlinedTextField(
                value         = amount,
                onValueChange = { amount = it },
                label         = { Text("Amount (₹) *", color = TextMuted) },
                leadingIcon   = {
                    Text("₹", fontSize = 16.sp, color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp))
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors        = expenseTextFieldColors()
            )

            OutlinedTextField(
                value         = reason,
                onValueChange = { reason = it },
                label         = { Text("Reason / Note", color = TextMuted) },
                placeholder   = {
                    Text("e.g. Restaurant bill, Auto fare, Emergency",
                        color = TextMuted)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            OutlinedTextField(
                value         = dueDate,
                onValueChange = { dueDate = it },
                label         = { Text("Expected Return Date (yyyy-mm-dd)", color = TextMuted) },
                leadingIcon   = {
                    Icon(Icons.Default.CalendarToday, null, tint = Gold)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            Button(
                onClick = {
                    val amt = amount.toFloatOrNull() ?: return@Button
                    if (name.isBlank()) return@Button
                    onSave(
                        LendBorrow(
                            personName = name.trim(),
                            personPhone = phone.trim(),
                            amount     = amt,
                            type       = type.name,
                            reason     = reason.trim(),
                            date       = currentDate(),
                            dueDate    = dueDate.trim(),
                            status     = DebtStatus.PENDING.name
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                enabled  = name.isNotBlank() && amount.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text(
                    if (isLent) "Save — They owe me ₹${amount.ifBlank { "0" }}"
                    else "Save — I owe ₹${amount.ifBlank { "0" }}",
                    color      = if (isLent) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun EmptyDebtCard(emoji: String, title: String, subtitle: String) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(emoji, fontSize = 40.sp)
            Text(title, color = TextMuted, fontSize = 15.sp,
                fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank())
                Text(subtitle, color = TextMuted, fontSize = 12.sp,
                    textAlign = TextAlign.Center)
        }
    }
}