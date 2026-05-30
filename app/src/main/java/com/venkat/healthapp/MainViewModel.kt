package com.venkat.healthapp

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.venkat.healthapp.auth.data.AppUser
import com.venkat.healthapp.auth.data.AuthRepository
import com.venkat.healthapp.auth.data.AuthState
import com.venkat.healthapp.common.AppDatabase
import com.venkat.healthapp.expense.data.*
import com.venkat.healthapp.expense.ui.cancelNoteReminder
import com.venkat.healthapp.expense.ui.scheduleNoteReminder
import com.venkat.healthapp.food.data.*
import com.venkat.healthapp.hair.data.*
import com.venkat.healthapp.sleep.data.SleepLog
import com.venkat.healthapp.sync.SyncManager
import com.venkat.healthapp.vault.data.VaultItem
import com.venkat.healthapp.water.data.*
import com.venkat.healthapp.workout.data.WorkoutLog
import com.venkat.healthapp.workout.data.getTodaySplit
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private var syncManager: SyncManager? = null
    private val authRepo = AuthRepository(app)

    private val _currentUser = MutableStateFlow<AppUser?>(authRepo.currentUser)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    val searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val _searchQuery  = MutableStateFlow("")

    val db    = AppDatabase.get(app)
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    // ── Sync ──────────────────────────────────────────────────────────────────
    fun initSync(userId: String) {
        syncManager = SyncManager(db, userId)
        viewModelScope.launch {
            syncManager?.restoreAll()   // Pull from cloud first
            syncManager?.syncAll()      // Push local data up
        }
    }

    fun syncToCloud() {
        viewModelScope.launch {
            syncManager?.syncAll()
        }
    }

    // ── Hair ──────────────────────────────────────────────────────────────────
    val todayTasks = db.taskLogDao().getTasksForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayProgress = todayTasks
        .map { Pair(it.count { t -> t.completed }, it.size) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0, 0))

    val allSummaries = db.dailySummaryDao().allSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perfectDays = db.dailySummaryDao().perfectDaysCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeDays = db.dailySummaryDao().activeDaysCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCompleted = db.dailySummaryDao().totalCompletedAllTime()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak = db.dailySummaryDao().datesWithActivity()
        .map { calcStreak(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Keep these for backward compat
    val totalDone = totalCompleted
    val streak    = currentStreak

    private val _shampooToday = MutableStateFlow(getDefaultShampoo())
    val shampooToday: StateFlow<String> = _shampooToday.asStateFlow()

    // Keep old name for compat
    val shampoo = _shampooToday

    fun setShampoo(v: String) { _shampooToday.value = v }

    private fun getDefaultShampoo(): String {
        return when (LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.TUESDAY,
            java.time.DayOfWeek.THURSDAY -> "hairex"
            else -> "kone"
        }
    }




    init {
        viewModelScope.launch {
            authRepo.authState.collect { state ->
                _currentUser.value = when (state) {
                    is AuthState.Authenticated -> state.user
                    else -> _currentUser.value  // keep existing on other states
                }
            }
        }
        viewModelScope.launch { initTasks() }
        viewModelScope.launch { seedFoodDb() }
        viewModelScope.launch {
            _searchQuery.debounce(300).collectLatest { q ->
                if (q.isBlank()) {
                    searchResults.value = emptyList()
                    return@collectLatest
                }
                db.foodItemDao().search(q).collect { searchResults.value = it }
            }
        }

    }

    private suspend fun initTasks() {
        ALL_TASKS.forEach { def ->
            db.taskLogDao().upsert(
                TaskLog(
                    date     = today,
                    taskId   = def.id,
                    taskName = def.name,
                    section  = def.section.name
                )
            )
        }
    }

    fun toggleTask(taskId: String, current: Boolean) {
        viewModelScope.launch {
            db.taskLogDao().updateTask(
                today, taskId, !current,
                if (!current) System.currentTimeMillis() else 0L
            )
            val tasks = todayTasks.value
            val done  = tasks.count { if (it.taskId == taskId) !current else it.completed }
            db.dailySummaryDao().upsert(
                DailySummary(today, ALL_TASKS.size, done.coerceAtLeast(0), _shampooToday.value)
            )
            syncToCloud()  // ← Sync after every task tick
        }
    }

    fun tasksBySection(tasks: List<TaskLog>) =
        Section.values().associateWith { sec ->
            ALL_TASKS.filter { it.section == sec }
                .map { def -> def to tasks.find { it.taskId == def.id } }
        }

    private fun calcStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        val sorted = dates.mapNotNull {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }.sortedDescending()
        var s = 0; var exp = LocalDate.now()
        for (d in sorted) {
            if (d == exp || d == exp.minusDays(1)) { s++; exp = d.minusDays(1) }
            else break
        }
        return s
    }

    fun logPastDay(date: String, completedCount: Int) {
        viewModelScope.launch {
            val dailyIds  = listOf("m1","m2","m3","m4","m5","a1","n1","n2","n3","n4")
            val weeklyIds = listOf("w1","w2","w3")
            ALL_TASKS.forEach { def ->
                val isDone = dailyIds.contains(def.id)
                db.taskLogDao().upsert(TaskLog(
                    date        = date,
                    taskId      = def.id,
                    taskName    = def.name,
                    section     = def.section.name,
                    completed   = isDone,
                    completedAt = if (isDone) System.currentTimeMillis() else 0L
                ))
            }
            db.dailySummaryDao().upsert(
                DailySummary(date, ALL_TASKS.size, dailyIds.size)
            )
            syncToCloud()
        }
    }

    // ── Food ──────────────────────────────────────────────────────────────────
    val userProfile = db.userProfileDao().get()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nutritionTargets = userProfile
        .map { p -> if (p != null) calculateTargets(p) else null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayFoodLogs = db.foodLogDao().logsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayNutrition = todayFoodLogs.map { logs ->
        mapOf(
            "calories" to logs.sumOf { it.calories.toDouble() }.toFloat(),
            "protein"  to logs.sumOf { it.protein.toDouble()  }.toFloat(),
            "carbs"    to logs.sumOf { it.carbs.toDouble()    }.toFloat(),
            "fat"      to logs.sumOf { it.fat.toDouble()      }.toFloat(),
            "fiber"    to logs.sumOf { it.fiber.toDouble()    }.toFloat()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())



    fun search(q: String) { _searchQuery.value = q }

    private suspend fun seedFoodDb() {
        if (db.foodItemDao().count() == 0) {
            db.foodItemDao().insertAll(indianFoodDatabase())
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            db.userProfileDao().upsert(profile)
            syncToCloud()
        }
    }

    fun logFood(item: FoodItem, qty: Float, meal: String) {
        viewModelScope.launch {
            db.foodLogDao().insert(FoodLog(
                date       = today,
                foodItemId = item.id,
                foodName   = item.name,
                quantity   = qty,
                unit       = item.unit,
                calories   = item.calories * qty,
                protein    = item.protein * qty,
                carbs      = item.carbs * qty,
                fat        = item.fat * qty,
                fiber      = item.fiber * qty,
                mealType   = meal
            ))
            syncToCloud()
        }
    }

    fun deleteFoodLog(log: FoodLog) {
        viewModelScope.launch {
            db.foodLogDao().delete(log)
            syncToCloud()
        }
    }

    fun addCustomFood(item: FoodItem) {
        viewModelScope.launch { db.foodItemDao().insert(item) }
    }

    fun allFoodItems() = db.foodItemDao().allItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Water ─────────────────────────────────────────────────────────────────
    val waterToday = db.waterLogDao().totalForDate(today)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val waterTarget = userProfile.map { p ->
        if (p != null && p.weightKg > 0)
            (p.weightKg * 33).toInt().coerceAtLeast(3000)
        else 3000
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3000)

    val waterLogs = db.waterLogDao().logsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWater(ml: Int) {
        viewModelScope.launch {
            db.waterLogDao().insert(WaterLog(date = today, amountMl = ml))
            syncToCloud()
        }
    }

    fun removeLastWater() {
        viewModelScope.launch {
            val logs = waterLogs.value
            if (logs.isNotEmpty()) {
                db.waterLogDao().delete(logs.last())
                syncToCloud()
            }
        }
    }

    fun applyPendingAutoReduce(context: android.content.Context) {
        val prefs   = context.getSharedPreferences("water_auto", android.content.Context.MODE_PRIVATE)
        val pending = prefs.getInt("pending_ml", 0)
        if (pending > 0) {
            val toAdd = pending.coerceAtMost(waterTarget.value - waterToday.value).coerceAtLeast(0)
            if (toAdd > 0) addWater(toAdd)
            prefs.edit().putInt("pending_ml", 0).apply()
        }
    }

    // ── Sleep ─────────────────────────────────────────────────────────────────
    val sleepLogs = db.sleepLogDao().allLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepLastSeven = db.sleepLogDao().lastSevenDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepAvgDuration = db.sleepLogDao().avgDurationMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sleepAvgQuality = db.sleepLogDao().avgQuality()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sleepGoodDays = db.sleepLogDao().goodSleepDaysCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySleepLog = db.sleepLogDao().logForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logSleep(bedTimeMillis: Long, wakeTimeMillis: Long, quality: Int, note: String) {
        viewModelScope.launch {
            val duration = ((wakeTimeMillis - bedTimeMillis) / 60000).toInt()
                .let { if (it < 0) it + 24 * 60 else it }
            db.sleepLogDao().insert(SleepLog(
                date            = today,
                bedTimeMillis   = bedTimeMillis,
                wakeTimeMillis  = wakeTimeMillis,
                durationMinutes = duration,
                quality         = quality,
                note            = note
            ))
            syncToCloud()
        }
    }

    fun deleteSleepLog(log: SleepLog) {
        viewModelScope.launch {
            db.sleepLogDao().delete(log)
            syncToCloud()
        }
    }

    // ── Workout ───────────────────────────────────────────────────────────────
    val workoutLogs = db.workoutLogDao().logsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutTotalDays = db.workoutLogDao().totalWorkoutDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val workoutDates = db.workoutLogDao().allDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleExercise(exerciseName: String, currentDone: Boolean) {
        viewModelScope.launch {
            if (currentDone) {
                val log = workoutLogs.value.find { it.exerciseName == exerciseName }
                if (log != null) db.workoutLogDao().delete(log)
            } else {
                db.workoutLogDao().insert(WorkoutLog(
                    date         = today,
                    splitName    = getTodaySplit().name,
                    exerciseName = exerciseName,
                    sets         = 0,
                    reps         = 0,
                    completed    = true
                ))
            }
            syncToCloud()
        }
    }

    // ── Expense ───────────────────────────────────────────────────────────────
    val todayExpenses = db.expenseDao().expensesForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotal = db.expenseDao().totalForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val monthExpenses = db.expenseDao().expensesForMonth(today.substring(0, 7))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthTotal = db.expenseDao().totalForMonth(today.substring(0, 7))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val categoryTotals = db.expenseDao().categoryTotalsForMonth(today.substring(0, 7))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesPendingNote = db.expenseDao().expensesPendingNote()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesWithoutNote = db.expenseDao().expensesWithoutNote()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthCount = db.expenseDao().countForMonth(today.substring(0, 7))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            val id = db.expenseDao().insert(expense)
            if (expense.noteReminderSet) {
                scheduleNoteReminder(app, id.toInt(), expense.title, expense.amount)
            }
            syncToCloud()
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            db.expenseDao().update(expense)
            if (expense.noteAdded) cancelNoteReminder(app, expense.id)
            syncToCloud()
        }
    }

    fun updateExpenseNote(expenseId: Int, note: String) {
        viewModelScope.launch {
            val expense = db.expenseDao().getById(expenseId) ?: return@launch
            db.expenseDao().update(expense.copy(
                note      = note,
                noteAdded = true,
                updatedAt = System.currentTimeMillis()
            ))
            cancelNoteReminder(app, expenseId)
            syncToCloud()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            cancelNoteReminder(app, expense.id)
            db.expenseDao().delete(expense)
            syncToCloud()
        }
    }

    // ── Lend & Borrow ─────────────────────────────────────────────────────────
    val pendingLent = db.lendBorrowDao().pendingLent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingBorrowed = db.lendBorrowDao().pendingBorrowed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settledDebts = db.lendBorrowDao().settled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalLentPending = db.lendBorrowDao().totalLentPending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalBorrowedPending = db.lendBorrowDao().totalBorrowedPending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addLendBorrow(entry: LendBorrow) {
        viewModelScope.launch {
            db.lendBorrowDao().insert(entry)
            syncToCloud()
        }
    }

    fun settleDebt(entry: LendBorrow) {
        viewModelScope.launch {
            db.lendBorrowDao().update(entry.copy(
                status    = DebtStatus.SETTLED.name,
                paidBack  = entry.amount,
                updatedAt = System.currentTimeMillis()
            ))
            syncToCloud()
        }
    }

    fun addPartialPayment(entry: LendBorrow, amount: Float) {
        viewModelScope.launch {
            val newPaid   = (entry.paidBack + amount).coerceAtMost(entry.amount)
            val newStatus = if (newPaid >= entry.amount)
                DebtStatus.SETTLED.name else DebtStatus.PARTIAL.name
            db.lendBorrowDao().update(entry.copy(
                paidBack  = newPaid,
                status    = newStatus,
                updatedAt = System.currentTimeMillis()
            ))
            db.partialPaymentDao().insert(
                PartialPayment(lendBorrowId = entry.id, amount = amount, date = today)
            )
            syncToCloud()
        }
    }

    fun deleteLendBorrow(entry: LendBorrow) {
        viewModelScope.launch {
            db.lendBorrowDao().delete(entry)
            syncToCloud()
        }
    }

    fun sendReminder(entry: LendBorrow) { /* Handled in UI */ }

    // ── Split Expense ─────────────────────────────────────────────────────────
    val allSplits = db.splitExpenseDao().allSplits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMembersForSplit(splitId: Int) =
        db.splitExpenseDao().membersForSplit(splitId)

    fun createSplit(split: SplitExpense, members: List<SplitMember>) {
        viewModelScope.launch {
            val id = db.splitExpenseDao().insertSplit(split)
            members.forEach { member ->
                db.splitExpenseDao().insertMember(member.copy(splitExpenseId = id.toInt()))
            }
        }
    }

    fun markMemberPaid(member: SplitMember) {
        viewModelScope.launch {
            db.splitExpenseDao().updateMember(
                member.copy(isPaid = true, paidAt = System.currentTimeMillis())
            )
        }
    }

    fun deleteSplit(split: SplitExpense) {
        viewModelScope.launch { db.splitExpenseDao().deleteSplit(split) }
    }

    // ── Vault (local only — never syncs to cloud for security) ────────────────
    val vaultItems = db.vaultDao().allItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultFavorites = db.vaultDao().favorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun vaultSearch(query: String) = if (query.isBlank())
        db.vaultDao().allItems()
    else
        db.vaultDao().search(query)

    fun addVaultItem(item: VaultItem) {
        viewModelScope.launch { db.vaultDao().insert(item) }
        // Vault intentionally NOT synced to cloud
    }

    fun updateVaultItem(item: VaultItem) {
        viewModelScope.launch { db.vaultDao().update(item) }
    }

    fun deleteVaultItem(item: VaultItem) {
        viewModelScope.launch { db.vaultDao().delete(item) }
    }

    fun toggleVaultFavorite(item: VaultItem) {
        viewModelScope.launch {
            db.vaultDao().update(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)?.await()
            // ✅ Now this works — update local StateFlow
            _currentUser.update { it?.copy(displayName = name) }
        }
    }
}