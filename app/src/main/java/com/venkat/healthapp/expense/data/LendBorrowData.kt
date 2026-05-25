package com.venkat.healthapp.expense.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Enums ─────────────────────────────────────────────────────────────────────
enum class MoneyType {
    LENT,       // I gave money to friend
    BORROWED    // I took money from friend
}

enum class DebtStatus {
    PENDING,    // not yet returned
    PARTIAL,    // partially returned
    SETTLED     // fully settled
}

// ── Entities ──────────────────────────────────────────────────────────────────
@Entity(tableName = "lend_borrow")
data class LendBorrow(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val personPhone: String = "",
    val amount: Float,
    val paidBack: Float = 0f,           // how much returned so far
    val type: String,                    // MoneyType.name
    val status: String = DebtStatus.PENDING.name,
    val reason: String = "",             // why lent/borrowed
    val date: String,                    // when transaction happened
    val dueDate: String = "",            // expected return date
    val reminderSet: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "partial_payment")
data class PartialPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lendBorrowId: Int,
    val amount: Float,
    val date: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "split_expense")
data class SplitExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val totalAmount: Float,
    val date: String,
    val paidBy: String = "Me",
    val note: String = "",
    val isSettled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "split_member")
data class SplitMember(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val splitExpenseId: Int,
    val name: String,
    val phone: String = "",
    val shareAmount: Float,
    val isPaid: Boolean = false,
    val paidAt: Long = 0L
)

// ── DAOs ──────────────────────────────────────────────────────────────────────
@Dao
interface LendBorrowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: LendBorrow): Long

    @Update
    suspend fun update(entry: LendBorrow)

    @Delete
    suspend fun delete(entry: LendBorrow)

    @Query("SELECT * FROM lend_borrow ORDER BY createdAt DESC")
    fun all(): Flow<List<LendBorrow>>

    @Query("SELECT * FROM lend_borrow WHERE type = 'LENT' AND status != 'SETTLED' ORDER BY createdAt DESC")
    fun pendingLent(): Flow<List<LendBorrow>>

    @Query("SELECT * FROM lend_borrow WHERE type = 'BORROWED' AND status != 'SETTLED' ORDER BY createdAt DESC")
    fun pendingBorrowed(): Flow<List<LendBorrow>>

    @Query("SELECT * FROM lend_borrow WHERE status = 'SETTLED' ORDER BY updatedAt DESC")
    fun settled(): Flow<List<LendBorrow>>

    @Query("SELECT SUM(amount - paidBack) FROM lend_borrow WHERE type = 'LENT' AND status != 'SETTLED'")
    fun totalLentPending(): Flow<Float?>

    @Query("SELECT SUM(amount - paidBack) FROM lend_borrow WHERE type = 'BORROWED' AND status != 'SETTLED'")
    fun totalBorrowedPending(): Flow<Float?>

    @Query("SELECT * FROM lend_borrow WHERE id = :id")
    suspend fun getById(id: Int): LendBorrow?
}

@Dao
interface PartialPaymentDao {
    @Insert
    suspend fun insert(p: PartialPayment)

    @Query("SELECT * FROM partial_payment WHERE lendBorrowId = :id ORDER BY createdAt DESC")
    fun paymentsForEntry(id: Int): Flow<List<PartialPayment>>
}

@Dao
interface SplitExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplit(s: SplitExpense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(m: SplitMember): Long

    @Update
    suspend fun updateSplit(s: SplitExpense)

    @Update
    suspend fun updateMember(m: SplitMember)

    @Delete
    suspend fun deleteSplit(s: SplitExpense)

    @Query("SELECT * FROM split_expense ORDER BY createdAt DESC")
    fun allSplits(): Flow<List<SplitExpense>>

    @Query("SELECT * FROM split_member WHERE splitExpenseId = :splitId")
    fun membersForSplit(splitId: Int): Flow<List<SplitMember>>

    @Query("SELECT * FROM split_expense WHERE id = :id")
    suspend fun getSplitById(id: Int): SplitExpense?

    @Query("SELECT * FROM split_member WHERE splitExpenseId = :id")
    suspend fun getMembersForSplit(id: Int): List<SplitMember>
}

// ── Helper data class for contact picker ─────────────────────────────────────
data class Contact(
    val name: String,
    val phone: String
)