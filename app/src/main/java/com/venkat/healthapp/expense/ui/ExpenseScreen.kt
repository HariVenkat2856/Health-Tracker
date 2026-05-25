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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(vm: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Today", "Monthly", "Add Note", "Analytics", "Lend/Borrow", "Split")

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
                        Text(t, fontSize = 13.sp,
                            fontWeight = if (selectedTab == i) FontWeight.Bold
                            else FontWeight.Normal,
                            color = if (selectedTab == i) Accent else TextMuted)
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> TodayExpenseTab(vm)
            1 -> MonthlyExpenseTab(vm)
            2 -> AddNoteTab(vm)
            3 -> AnalyticsTab(vm)
            4 -> LendBorrowScreen(vm)
            5 -> SplitExpenseScreen(vm)
        }
    }
}

// ── TODAY tab ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayExpenseTab(vm: MainViewModel) {
    val todayExpenses   by vm.todayExpenses.collectAsState()
    val todayTotal      by vm.todayTotal.collectAsState()
    val pendingNotes    by vm.expensesPendingNote.collectAsState()
    var showAddSheet    by remember { mutableStateOf(false) }
    var editExpense     by remember { mutableStateOf<Expense?>(null) }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today's Expenses",
                        fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(formatDate(currentDate()), color = TextMuted, fontSize = 13.sp)
                }
                FloatingActionButton(
                    onClick          = { showAddSheet = true },
                    containerColor   = Accent,
                    contentColor     = Color.Black,
                    shape            = CircleShape,
                    modifier         = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(24.dp))
                }
            }
        }

        // ── Today total card ──────────────────────────────────────────────────
        item {
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF0D2A1F), Color(0xFF0A1628)))
                    )
                    .border(1.dp, Accent.copy(0.35f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Spent Today", fontSize = 13.sp, color = TextMuted)
                    Text(
                        formatAmountFull(todayTotal ?: 0f),
                        fontSize   = 38.sp,
                        fontWeight = FontWeight.Black,
                        color      = if ((todayTotal ?: 0f) > 1000) RedPill else Accent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${todayExpenses.size} transactions",
                        fontSize = 12.sp, color = TextMuted
                    )
                }
            }
        }

        // ── Pending notes warning ─────────────────────────────────────────────
        if (pendingNotes.isNotEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GoldAlpha)
                        .border(1.dp, Gold.copy(0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 20.sp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                "${pendingNotes.size} expense(s) waiting for notes!",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Gold
                            )
                            Text(
                                "You said you'd add notes later — do it now!",
                                fontSize = 12.sp, color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // ── Expense list ──────────────────────────────────────────────────────
        if (todayExpenses.isEmpty()) {
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💰", fontSize = 40.sp)
                        Text("No expenses today", color = TextMuted)
                        Text("Tap + to add one", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(todayExpenses) { expense ->
                ExpenseCard(
                    expense  = expense,
                    onEdit   = { editExpense = expense },
                    onDelete = { vm.deleteExpense(expense) }
                )
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }

    // ── Add expense sheet ─────────────────────────────────────────────────────
    if (showAddSheet) {
        AddExpenseSheet(
            onDismiss = { showAddSheet = false },
            onSave    = { expense -> vm.addExpense(expense); showAddSheet = false }
        )
    }

    // ── Edit expense sheet ────────────────────────────────────────────────────
    editExpense?.let { exp ->
        AddExpenseSheet(
            existing  = exp,
            onDismiss = { editExpense = null },
            onSave    = { updated -> vm.updateExpense(updated); editExpense = null }
        )
    }
}

// ── Expense card ──────────────────────────────────────────────────────────────
@Composable
fun ExpenseCard(expense: Expense, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cat     = getCategoryEnum(expense.category)
    val payment = getPaymentModeEnum(expense.paymentMode)
    val needsNote = expense.note.isBlank()

    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (needsNote) Color(0xFF1A1200) else CardDark)
            .border(
                1.dp,
                if (needsNote) Gold.copy(0.4f) else BorderDark,
                RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category emoji circle
            Box(
                Modifier.size(44.dp)
                    .clip(CircleShape)
                    .background(Accent.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(cat.emoji, fontSize = 20.sp)
            }

            Column(Modifier.weight(1f)) {
                Text(
                    expense.title,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(cat.label, fontSize = 11.sp, color = TextMuted)
                    Text("•", fontSize = 11.sp, color = TextMuted)
                    Text(payment.emoji + " " + payment.label,
                        fontSize = 11.sp, color = TextMuted)
                }
                if (expense.note.isNotBlank()) {
                    Text(
                        expense.note,
                        fontSize  = 12.sp,
                        color     = TextMuted,
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis
                    )
                } else {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.NoteAdd, null,
                            tint = Gold, modifier = Modifier.size(12.dp))
                        Text("No note added — tap to add",
                            fontSize = 11.sp, color = Gold)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatAmountFull(expense.amount),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Text(
                    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(expense.createdAt)),
                    fontSize = 10.sp, color = TextMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, null,
                            tint = Accent, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null,
                            tint = RedPill, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ── Add / Edit expense bottom sheet ──────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    existing: Expense? = null,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var amount      by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
    var title       by remember { mutableStateOf(existing?.title ?: "") }
    var note        by remember { mutableStateOf(existing?.note ?: "") }
    var category    by remember { mutableStateOf(existing?.category ?: ExpenseCategory.OTHER.name) }
    var paymentMode by remember { mutableStateOf(existing?.paymentMode ?: PaymentMode.UPI.name) }
    var addNoteLater by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                if (existing != null) "Edit Expense" else "Add Expense",
                fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary
            )

            // ── Amount ────────────────────────────────────────────────────────
            OutlinedTextField(
                value         = amount,
                onValueChange = { amount = it },
                label         = { Text("Amount (₹) *", color = TextMuted) },
                leadingIcon   = { Text("₹", fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = Accent,
                    modifier = Modifier.padding(start = 12.dp)) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors        = expenseTextFieldColors()
            )

            // ── Title ─────────────────────────────────────────────────────────
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                label         = { Text("What did you spend on? *", color = TextMuted) },
                placeholder   = { Text("e.g. Pharmacy, Auto, Lunch", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = expenseTextFieldColors()
            )

            // ── Category selector ─────────────────────────────────────────────
            Text("Category", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExpenseCategory.values()) { cat ->
                    val sel = category == cat.name
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) AccentAlpha else Card2Dark)
                            .border(1.dp,
                                if (sel) Accent else BorderDark,
                                RoundedCornerShape(10.dp))
                            .clickable { category = cat.name }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(cat.emoji, fontSize = 18.sp)
                            Text(cat.label.split(" ").first(),
                                fontSize = 10.sp,
                                color = if (sel) Accent else TextMuted,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // ── Payment mode ──────────────────────────────────────────────────
            Text("Payment Mode", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMode.values().forEach { mode ->
                    val sel = paymentMode == mode.name
                    Box(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) AccentAlpha else Card2Dark)
                            .border(1.dp,
                                if (sel) Accent else BorderDark,
                                RoundedCornerShape(10.dp))
                            .clickable { paymentMode = mode.name }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(mode.emoji, fontSize = 16.sp)
                            Text(mode.label,
                                fontSize = 9.sp,
                                color = if (sel) Accent else TextMuted,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // ── Note section ──────────────────────────────────────────────────
            // This is the KEY feature — handle "add note later"
            Text("Note", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)

            OutlinedTextField(
                value         = note,
                onValueChange = { note = it; if (it.isNotBlank()) addNoteLater = false },
                label         = { Text("What was this for? (details)", color = TextMuted) },
                placeholder   = { Text(
                    "e.g. Bought T.Minodez + T.Sandro from Apollo pharmacy\n" +
                            "Or leave blank and set reminder below",
                    color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                minLines      = 2,
                maxLines      = 4,
                colors        = expenseTextFieldColors()
            )

            // ── "Remind me to add note" toggle ────────────────────────────────
            if (note.isBlank()) {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (addNoteLater) GoldAlpha else Card2Dark)
                        .border(1.dp,
                            if (addNoteLater) Gold.copy(0.5f) else BorderDark,
                            RoundedCornerShape(12.dp))
                        .clickable { addNoteLater = !addNoteLater }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⏰", fontSize = 22.sp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Remind me to add note later",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (addNoteLater) Gold else TextPrimary
                            )
                            Text(
                                "You'll get a notification in 2 hours reminding you to fill in the details",
                                fontSize = 12.sp,
                                color    = TextMuted
                            )
                        }
                        Checkbox(
                            checked         = addNoteLater,
                            onCheckedChange = { addNoteLater = it },
                            colors          = CheckboxDefaults.colors(
                                checkedColor   = Gold,
                                uncheckedColor = TextMuted
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Save button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    val amt = amount.toFloatOrNull() ?: return@Button
                    if (title.isBlank()) return@Button
                    val expense = (existing ?: Expense(
                        date        = currentDate(),
                        amount      = amt,
                        category    = category,
                        paymentMode = paymentMode,
                        title       = title,
                        note        = note,
                        noteReminderSet = addNoteLater && note.isBlank(),
                        noteAdded   = note.isNotBlank(),
                        createdAt   = System.currentTimeMillis(),
                        updatedAt   = System.currentTimeMillis()
                    )).let {
                        if (existing != null) it.copy(
                            amount      = amt,
                            category    = category,
                            paymentMode = paymentMode,
                            title       = title,
                            note        = note,
                            noteAdded   = note.isNotBlank(),
                            noteReminderSet = addNoteLater && note.isBlank(),
                            updatedAt   = System.currentTimeMillis()
                        ) else it
                    }
                    onSave(expense)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                enabled  = amount.isNotBlank() && title.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Icon(Icons.Default.Save, null,
                    tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (existing != null) "Update Expense" else "Save Expense",
                    color      = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun expenseTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Accent,
    unfocusedBorderColor = BorderDark,
    focusedTextColor     = TextPrimary,
    unfocusedTextColor   = TextPrimary,
    cursorColor          = Accent,
    focusedContainerColor   = Card2Dark,
    unfocusedContainerColor = Card2Dark
)

// ── MONTHLY tab ───────────────────────────────────────────────────────────────
@Composable
fun MonthlyExpenseTab(vm: MainViewModel) {
    val monthExpenses by vm.monthExpenses.collectAsState()
    val monthTotal    by vm.monthTotal.collectAsState()
    val catTotals     by vm.categoryTotals.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        item {
            Text("This Month", fontSize = 22.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text(java.text.SimpleDateFormat("MMMM yyyy",
                java.util.Locale.getDefault()).format(java.util.Date()),
                color = TextMuted, fontSize = 13.sp)
        }

        // Monthly total
        item {
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(
                        listOf(Color(0xFF1A0D28), Color(0xFF0D1117))
                    ))
                    .border(1.dp, Purple.copy(0.4f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Spent", fontSize = 13.sp, color = TextMuted)
                        Text(
                            formatAmountFull(monthTotal ?: 0f),
                            fontSize   = 34.sp,
                            fontWeight = FontWeight.Black,
                            color      = Purple
                        )
                        Text("${monthExpenses.size} transactions",
                            fontSize = 12.sp, color = TextMuted)
                    }
                    Text("💸", fontSize = 48.sp)
                }
            }
        }

        // Category breakdown
        if (catTotals.isNotEmpty()) {
            item {
                Text("By Category", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val totalSpent = monthTotal ?: 1f
                        catTotals.forEach { ct ->
                            val cat = getCategoryEnum(ct.category)
                            val pct = ct.total / totalSpent
                            val catColor = categoryColor(ct.category)
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(cat.emoji, fontSize = 20.sp)
                                Column(Modifier.weight(1f)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(cat.label, fontSize = 13.sp, color = TextPrimary)
                                        Text(formatAmountFull(ct.total),
                                            fontSize   = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = catColor)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        Modifier.fillMaxWidth().height(5.dp)
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(BorderDark)
                                    ) {
                                        Box(
                                            Modifier.fillMaxWidth(pct).height(5.dp)
                                                .background(catColor, RoundedCornerShape(100.dp))
                                        )
                                    }
                                }
                                Text("${(pct * 100).toInt()}%",
                                    fontSize = 11.sp, color = TextMuted,
                                    modifier = Modifier.width(32.dp),
                                    textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }

        // All expenses this month
        item {
            Text("All Transactions", fontSize = 16.sp,
                fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        items(monthExpenses) { expense ->
            ExpenseCard(
                expense  = expense,
                onEdit   = { },
                onDelete = { vm.deleteExpense(expense) }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── ADD NOTE tab — the key feature ───────────────────────────────────────────
@Composable
fun AddNoteTab(vm: MainViewModel) {
    val noNoteExpenses by vm.expensesWithoutNote.collectAsState()
    var editingId      by remember { mutableStateOf<Int?>(null) }
    var noteText       by remember { mutableStateOf("") }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        item {
            Text("Add Notes", fontSize = 22.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Expenses waiting for your description",
                color = TextMuted, fontSize = 13.sp)
        }

        if (noNoteExpenses.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, Accent.copy(0.3f), RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✅", fontSize = 40.sp)
                        Text("All notes added!", color = Accent,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Great job keeping track!", color = TextMuted)
                    }
                }
            }
        } else {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAlpha)
                        .border(1.dp, Gold.copy(0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "💡 ${noNoteExpenses.size} expenses need notes. " +
                                "Adding notes helps you remember what you spent!",
                        fontSize = 13.sp, color = Gold
                    )
                }
            }

            items(noNoteExpenses) { expense ->
                val isEditing = editingId == expense.id
                val cat = getCategoryEnum(expense.category)

                Column(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isEditing) Card2Dark else CardDark)
                        .border(
                            1.dp,
                            if (isEditing) Accent.copy(0.5f) else Gold.copy(0.3f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Expense info
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(cat.emoji, fontSize = 22.sp)
                        Column(Modifier.weight(1f)) {
                            Text(expense.title,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = TextPrimary)
                            Text(
                                "${formatDate(expense.date)}  •  " +
                                        "${cat.label}  •  " +
                                        getPaymentModeEnum(expense.paymentMode).label,
                                fontSize = 11.sp, color = TextMuted
                            )
                        }
                        Text(
                            formatAmountFull(expense.amount),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary
                        )
                    }

                    if (isEditing) {
                        // Note input
                        OutlinedTextField(
                            value         = noteText,
                            onValueChange = { noteText = it },
                            placeholder   = {
                                Text(
                                    "What was this for? e.g. Bought medicines from Apollo, paid auto to hospital...",
                                    color = TextMuted, fontSize = 12.sp
                                )
                            },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            minLines      = 2,
                            maxLines      = 4,
                            colors        = expenseTextFieldColors()
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick  = { editingId = null; noteText = "" },
                                modifier = Modifier.weight(1f),
                                shape    = RoundedCornerShape(10.dp),
                                border   = BorderStroke(1.dp, BorderDark)
                            ) {
                                Text("Cancel", color = TextMuted)
                            }
                            Button(
                                onClick = {
                                    if (noteText.isNotBlank()) {
                                        vm.updateExpenseNote(expense.id, noteText)
                                        editingId = null
                                        noteText = ""
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = Accent)
                            ) {
                                Text("Save Note",
                                    color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = { editingId = expense.id; noteText = "" },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = Gold.copy(0.15f))
                        ) {
                            Icon(Icons.Default.NoteAdd, null,
                                tint = Gold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Note Now", color = Gold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── ANALYTICS tab ─────────────────────────────────────────────────────────────
@Composable
fun AnalyticsTab(vm: MainViewModel) {
    val monthTotal   by vm.monthTotal.collectAsState()
    val catTotals    by vm.categoryTotals.collectAsState()
    val monthCount   by vm.monthCount.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Text("Analytics", fontSize = 22.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
        }

        // Summary stats
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticCard(
                    "💸", formatAmount(monthTotal ?: 0f),
                    "This Month", Purple, Modifier.weight(1f)
                )
                AnalyticCard(
                    "🧾", "$monthCount",
                    "Transactions", AccentBlue, Modifier.weight(1f)
                )
                AnalyticCard(
                    "📊",
                    if (monthCount > 0)
                        formatAmount((monthTotal ?: 0f) / monthCount)
                    else "₹0",
                    "Avg/Day", Gold, Modifier.weight(1f)
                )
            }
        }

        // Top spending category
        if (catTotals.isNotEmpty()) {
            item {
                val top = catTotals.first()
                val cat = getCategoryEnum(top.category)
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(RedAlpha)
                        .border(1.dp, RedPill.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(cat.emoji, fontSize = 32.sp)
                        Column(Modifier.weight(1f)) {
                            Text("Top Spending",
                                fontSize = 12.sp, color = TextMuted)
                            Text(cat.label,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary)
                        }
                        Text(
                            formatAmountFull(top.total),
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Black,
                            color      = RedPill
                        )
                    }
                }
            }
        }

        // All categories pie-style
        item {
            Text("Category Breakdown", fontSize = 16.sp,
                fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        items(catTotals) { ct ->
            val cat      = getCategoryEnum(ct.category)
            val pct      = (ct.total / (monthTotal ?: 1f))
            val catColor = categoryColor(ct.category)
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(14.dp, 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(catColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(cat.emoji, fontSize = 18.sp)
                }
                Column(Modifier.weight(1f)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat.label, fontSize = 14.sp, color = TextPrimary)
                        Text(formatAmountFull(ct.total),
                            fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = catColor)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier.fillMaxWidth().height(4.dp)
                            .clip(RoundedCornerShape(100.dp)).background(BorderDark)
                    ) {
                        Box(
                            Modifier.fillMaxWidth(pct).height(4.dp)
                                .background(catColor, RoundedCornerShape(100.dp))
                        )
                    }
                    Text("${(pct * 100).toInt()}% of total",
                        fontSize = 10.sp, color = TextMuted)
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun AnalyticCard(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp)).background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp)).padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
        }
    }
}

fun categoryColor(category: String): Color = when (category) {
    "FOOD"          -> Gold
    "MEDICINE"      -> Accent
    "TRANSPORT"     -> AccentBlue
    "SHOPPING"      -> Purple
    "HEALTH"        -> Color(0xFF4CAF50)
    "GYM"           -> Color(0xFFFF9800)
    "ENTERTAINMENT" -> RedPill
    "BILLS"         -> Color(0xFF00BCD4)
    "GROCERIES"     -> Color(0xFF8BC34A)
    "EDUCATION"     -> Color(0xFF9C27B0)
    "PERSONAL"      -> Color(0xFFE91E63)
    else            -> TextMuted
}