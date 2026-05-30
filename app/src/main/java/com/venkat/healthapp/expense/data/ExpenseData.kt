package com.venkat.healthapp.expense.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Enums ─────────────────────────────────────────────────────────────────────
enum class ExpenseCategory(val emoji: String, val label: String) {
    FOOD("🍽", "Food & Dining"),
    MEDICINE("💊", "Medicine"),
    TRANSPORT("🚗", "Transport"),
    SHOPPING("🛍", "Shopping"),
    HEALTH("🏥", "Health & Doctor"),
    GYM("🏋️", "Gym & Fitness"),
    ENTERTAINMENT("🎬", "Entertainment"),
    BILLS("📱", "Bills & Recharge"),
    GROCERIES("🛒", "Groceries"),
    EDUCATION("📚", "Education"),
    PERSONAL("💈", "Personal Care"),
    OTHER("💰", "Other")
}

enum class PaymentMode(val emoji: String, val label: String) {
    CASH("💵", "Cash"),
    UPI("📱", "UPI"),
    CARD("💳", "Card"),
    NET_BANKING("🏦", "Net Banking"),
    WALLET("👛", "Wallet")
}

// ── Entities ──────────────────────────────────────────────────────────────────
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,                   // "2026-05-20"
    val amount: Float,
    val category: String,               // ExpenseCategory.name
    val paymentMode: String,            // PaymentMode.name
    val title: String,                  // quick title e.g. "Pharmacy"
    val note: String = "",              // detailed note — can add later
    val noteReminderSet: Boolean = false, // reminder to add note later
    val noteAdded: Boolean = false,     // user has added the note
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val tags: String = ""               // comma separated tags
)

@Entity(tableName = "expense_budget")
data class ExpenseBudget(
    @PrimaryKey val category: String,
    val monthlyLimit: Float,
    val month: String   // "2026-05"
)

@Entity(tableName = "expense_reminder")
data class ExpenseReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseId: Int,
    val reminderAt: Long,       // epoch millis when to remind
    val message: String,
    val isDone: Boolean = false
)

// ── DAOs ──────────────────────────────────────────────────────────────────────
@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun allExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY createdAt DESC")
    fun expensesForDate(date: String): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE date LIKE :monthPrefix || '%' 
        ORDER BY createdAt DESC
    """)
    fun expensesForMonth(monthPrefix: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :monthPrefix || '%'")
    fun totalForMonth(monthPrefix: String): Flow<Float?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    fun totalForDate(date: String): Flow<Float?>

    @Query("""
        SELECT category, SUM(amount) as total 
        FROM expenses 
        WHERE date LIKE :monthPrefix || '%' 
        GROUP BY category 
        ORDER BY total DESC
    """)
    fun categoryTotalsForMonth(monthPrefix: String): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM expenses WHERE noteAdded = 0 AND noteReminderSet = 1 ORDER BY createdAt DESC")
    fun expensesPendingNote(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE noteAdded = 0 AND note = '' ORDER BY createdAt DESC LIMIT 10")
    fun expensesWithoutNote(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Int): Expense?

    @Query("SELECT * FROM expenses WHERE date BETWEEN :from AND :to ORDER BY createdAt DESC")
    fun expensesBetween(from: String, to: String): Flow<List<Expense>>

    @Query("SELECT COUNT(*) FROM expenses WHERE date LIKE :monthPrefix || '%'")
    fun countForMonth(monthPrefix: String): Flow<Int>


}

@Dao
interface ExpenseBudgetDao {
    @Upsert
    suspend fun upsert(budget: ExpenseBudget)

    @Query("SELECT * FROM expense_budget WHERE month = :month")
    fun budgetsForMonth(month: String): Flow<List<ExpenseBudget>>
}

@Dao
interface ExpenseReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ExpenseReminder): Long

    @Update
    suspend fun update(reminder: ExpenseReminder)

    @Query("SELECT * FROM expense_reminder WHERE isDone = 0 ORDER BY reminderAt")
    fun pendingReminders(): Flow<List<ExpenseReminder>>

    @Query("UPDATE expense_reminder SET isDone = 1 WHERE expenseId = :expenseId")
    suspend fun markDone(expenseId: Int)
}

// ── Helper data class for category aggregation ────────────────────────────────
data class CategoryTotal(
    val category: String,
    val total: Float
)

// ── Helper functions ──────────────────────────────────────────────────────────
fun formatAmount(amount: Float): String {
    return if (amount >= 1000)
        "₹%.1fk".format(amount / 1000)
    else
        "₹%.0f".format(amount)
}

fun formatAmountFull(amount: Float): String = "₹%.2f".format(amount)

fun getCategoryEnum(name: String) =
    ExpenseCategory.values().find { it.name == name } ?: ExpenseCategory.OTHER

fun getPaymentModeEnum(name: String) =
    PaymentMode.values().find { it.name == name } ?: PaymentMode.CASH

fun currentMonth(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}

fun currentDate(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}

fun formatDate(date: String): String = try {
    val inp = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val out = java.text.SimpleDateFormat("EEE, dd MMM", java.util.Locale.getDefault())
    out.format(inp.parse(date)!!)
} catch (e: Exception) { date }

fun formatDateTime(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}