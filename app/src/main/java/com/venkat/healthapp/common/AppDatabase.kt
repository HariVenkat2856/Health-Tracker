package com.venkat.healthapp.common

import androidx.room.*
import com.venkat.healthapp.expense.data.Expense
import com.venkat.healthapp.expense.data.ExpenseBudget
import com.venkat.healthapp.expense.data.ExpenseBudgetDao
import com.venkat.healthapp.expense.data.ExpenseDao
import com.venkat.healthapp.expense.data.ExpenseReminder
import com.venkat.healthapp.expense.data.ExpenseReminderDao
import com.venkat.healthapp.expense.data.LendBorrow
import com.venkat.healthapp.expense.data.LendBorrowDao
import com.venkat.healthapp.expense.data.PartialPayment
import com.venkat.healthapp.expense.data.PartialPaymentDao
import com.venkat.healthapp.expense.data.SplitExpense
import com.venkat.healthapp.expense.data.SplitExpenseDao
import com.venkat.healthapp.expense.data.SplitMember
import com.venkat.healthapp.expense.receipt.Receipt
import com.venkat.healthapp.expense.receipt.ReceiptDao
import com.venkat.healthapp.food.data.*
import com.venkat.healthapp.hair.data.*
import com.venkat.healthapp.sleep.data.SleepLog
import com.venkat.healthapp.sleep.data.SleepLogDao
import com.venkat.healthapp.vault.data.VaultDao
import com.venkat.healthapp.vault.data.VaultItem
import com.venkat.healthapp.water.data.*
import com.venkat.healthapp.workout.data.WorkoutLog
import com.venkat.healthapp.workout.data.WorkoutLogDao
import com.venkat.healthapp.workout.data.WorkoutProgress
import com.venkat.healthapp.workout.data.WorkoutProgressDao

@Database(
    entities = [
        TaskLog::class, DailySummary::class, ScalpPhoto::class,
        FoodItem::class, FoodLog::class, UserProfile::class,
        WaterLog::class,
        SleepLog::class,
        WorkoutLog::class,
        WorkoutProgress::class,
        Expense::class,
        ExpenseBudget::class,
        ExpenseReminder::class,
        LendBorrow::class,
        PartialPayment::class,
        SplitExpense::class,
        SplitMember::class,
        VaultItem::class,
        Receipt::class

    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskLogDao(): TaskLogDao
    abstract fun dailySummaryDao(): DailySummaryDao
    abstract fun scalpPhotoDao(): ScalpPhotoDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun sleepLogDao(): SleepLogDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun workoutProgressDao(): WorkoutProgressDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseBudgetDao(): ExpenseBudgetDao
    abstract fun expenseReminderDao(): ExpenseReminderDao
    abstract fun lendBorrowDao(): LendBorrowDao
    abstract fun partialPaymentDao(): PartialPaymentDao
    abstract fun splitExpenseDao(): SplitExpenseDao
    abstract fun vaultDao(): VaultDao
    abstract fun receiptDao(): ReceiptDao


    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: android.content.Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "health_app_db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
