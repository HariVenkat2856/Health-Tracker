package com.venkat.healthapp.common;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.venkat.healthapp.expense.data.ExpenseBudgetDao;
import com.venkat.healthapp.expense.data.ExpenseBudgetDao_Impl;
import com.venkat.healthapp.expense.data.ExpenseDao;
import com.venkat.healthapp.expense.data.ExpenseDao_Impl;
import com.venkat.healthapp.expense.data.ExpenseReminderDao;
import com.venkat.healthapp.expense.data.ExpenseReminderDao_Impl;
import com.venkat.healthapp.expense.data.LendBorrowDao;
import com.venkat.healthapp.expense.data.LendBorrowDao_Impl;
import com.venkat.healthapp.expense.data.PartialPaymentDao;
import com.venkat.healthapp.expense.data.PartialPaymentDao_Impl;
import com.venkat.healthapp.expense.data.SplitExpenseDao;
import com.venkat.healthapp.expense.data.SplitExpenseDao_Impl;
import com.venkat.healthapp.expense.receipt.ReceiptDao;
import com.venkat.healthapp.expense.receipt.ReceiptDao_Impl;
import com.venkat.healthapp.food.data.FoodItemDao;
import com.venkat.healthapp.food.data.FoodItemDao_Impl;
import com.venkat.healthapp.food.data.FoodLogDao;
import com.venkat.healthapp.food.data.FoodLogDao_Impl;
import com.venkat.healthapp.food.data.UserProfileDao;
import com.venkat.healthapp.food.data.UserProfileDao_Impl;
import com.venkat.healthapp.hair.data.DailySummaryDao;
import com.venkat.healthapp.hair.data.DailySummaryDao_Impl;
import com.venkat.healthapp.hair.data.ScalpPhotoDao;
import com.venkat.healthapp.hair.data.ScalpPhotoDao_Impl;
import com.venkat.healthapp.hair.data.TaskLogDao;
import com.venkat.healthapp.hair.data.TaskLogDao_Impl;
import com.venkat.healthapp.sleep.data.SleepLogDao;
import com.venkat.healthapp.sleep.data.SleepLogDao_Impl;
import com.venkat.healthapp.vault.data.VaultDao;
import com.venkat.healthapp.vault.data.VaultDao_Impl;
import com.venkat.healthapp.water.data.WaterLogDao;
import com.venkat.healthapp.water.data.WaterLogDao_Impl;
import com.venkat.healthapp.workout.data.WorkoutLogDao;
import com.venkat.healthapp.workout.data.WorkoutLogDao_Impl;
import com.venkat.healthapp.workout.data.WorkoutProgressDao;
import com.venkat.healthapp.workout.data.WorkoutProgressDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TaskLogDao _taskLogDao;

  private volatile DailySummaryDao _dailySummaryDao;

  private volatile ScalpPhotoDao _scalpPhotoDao;

  private volatile FoodItemDao _foodItemDao;

  private volatile FoodLogDao _foodLogDao;

  private volatile UserProfileDao _userProfileDao;

  private volatile WaterLogDao _waterLogDao;

  private volatile SleepLogDao _sleepLogDao;

  private volatile WorkoutLogDao _workoutLogDao;

  private volatile WorkoutProgressDao _workoutProgressDao;

  private volatile ExpenseDao _expenseDao;

  private volatile ExpenseBudgetDao _expenseBudgetDao;

  private volatile ExpenseReminderDao _expenseReminderDao;

  private volatile LendBorrowDao _lendBorrowDao;

  private volatile PartialPaymentDao _partialPaymentDao;

  private volatile SplitExpenseDao _splitExpenseDao;

  private volatile VaultDao _vaultDao;

  private volatile ReceiptDao _receiptDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `task_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `taskId` TEXT NOT NULL, `taskName` TEXT NOT NULL, `section` TEXT NOT NULL, `completed` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_task_logs_date_taskId` ON `task_logs` (`date`, `taskId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_summary` (`date` TEXT NOT NULL, `totalTasks` INTEGER NOT NULL, `completedTasks` INTEGER NOT NULL, `shampooUsed` TEXT NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`date`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scalp_photos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `weekLabel` TEXT NOT NULL, `date` TEXT NOT NULL, `capturedAt` INTEGER NOT NULL, `photoPath1` TEXT NOT NULL, `photoPath2` TEXT NOT NULL, `label` TEXT NOT NULL, `weekNumber` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `unit` TEXT NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, `isCustom` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `foodItemId` INTEGER NOT NULL, `foodName` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, `mealType` TEXT NOT NULL, `loggedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `weightKg` REAL NOT NULL, `heightCm` REAL NOT NULL, `age` INTEGER NOT NULL, `gender` TEXT NOT NULL, `activityLevel` TEXT NOT NULL, `goal` TEXT NOT NULL, `targetWeightKg` REAL NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `water_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `amountMl` INTEGER NOT NULL, `loggedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `bedTimeMillis` INTEGER NOT NULL, `wakeTimeMillis` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `quality` INTEGER NOT NULL, `note` TEXT NOT NULL, `deepSleepMinutes` INTEGER NOT NULL, `loggedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `splitName` TEXT NOT NULL, `exerciseName` TEXT NOT NULL, `sets` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weightKg` REAL NOT NULL, `durationMinutes` INTEGER NOT NULL, `notes` TEXT NOT NULL, `completed` INTEGER NOT NULL, `loggedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `splitCompleted` TEXT NOT NULL, `totalExercises` INTEGER NOT NULL, `completedExercises` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `caloriesBurned` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, `category` TEXT NOT NULL, `paymentMode` TEXT NOT NULL, `title` TEXT NOT NULL, `note` TEXT NOT NULL, `noteReminderSet` INTEGER NOT NULL, `noteAdded` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isRecurring` INTEGER NOT NULL, `tags` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expense_budget` (`category` TEXT NOT NULL, `monthlyLimit` REAL NOT NULL, `month` TEXT NOT NULL, PRIMARY KEY(`category`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expense_reminder` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `expenseId` INTEGER NOT NULL, `reminderAt` INTEGER NOT NULL, `message` TEXT NOT NULL, `isDone` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lend_borrow` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `personName` TEXT NOT NULL, `personPhone` TEXT NOT NULL, `amount` REAL NOT NULL, `paidBack` REAL NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `reason` TEXT NOT NULL, `date` TEXT NOT NULL, `dueDate` TEXT NOT NULL, `reminderSet` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `partial_payment` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lendBorrowId` INTEGER NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `note` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `split_expense` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `totalAmount` REAL NOT NULL, `date` TEXT NOT NULL, `paidBy` TEXT NOT NULL, `note` TEXT NOT NULL, `isSettled` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `split_member` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `splitExpenseId` INTEGER NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `shareAmount` REAL NOT NULL, `isPaid` INTEGER NOT NULL, `paidAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vault_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `username` TEXT NOT NULL, `encryptedPassword` TEXT NOT NULL, `encryptedExtra` TEXT NOT NULL, `note` TEXT NOT NULL, `website` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `receipts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `expenseId` INTEGER NOT NULL, `imagePath` TEXT NOT NULL, `ocrText` TEXT NOT NULL, `detectedAmount` REAL NOT NULL, `detectedDate` TEXT NOT NULL, `detectedMerchant` TEXT NOT NULL, `note` TEXT NOT NULL, `capturedAt` INTEGER NOT NULL, `date` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0412b8f781ff9e30ad42704c0a79ce74')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `task_logs`");
        db.execSQL("DROP TABLE IF EXISTS `daily_summary`");
        db.execSQL("DROP TABLE IF EXISTS `scalp_photos`");
        db.execSQL("DROP TABLE IF EXISTS `food_items`");
        db.execSQL("DROP TABLE IF EXISTS `food_logs`");
        db.execSQL("DROP TABLE IF EXISTS `user_profile`");
        db.execSQL("DROP TABLE IF EXISTS `water_logs`");
        db.execSQL("DROP TABLE IF EXISTS `sleep_logs`");
        db.execSQL("DROP TABLE IF EXISTS `workout_logs`");
        db.execSQL("DROP TABLE IF EXISTS `workout_progress`");
        db.execSQL("DROP TABLE IF EXISTS `expenses`");
        db.execSQL("DROP TABLE IF EXISTS `expense_budget`");
        db.execSQL("DROP TABLE IF EXISTS `expense_reminder`");
        db.execSQL("DROP TABLE IF EXISTS `lend_borrow`");
        db.execSQL("DROP TABLE IF EXISTS `partial_payment`");
        db.execSQL("DROP TABLE IF EXISTS `split_expense`");
        db.execSQL("DROP TABLE IF EXISTS `split_member`");
        db.execSQL("DROP TABLE IF EXISTS `vault_items`");
        db.execSQL("DROP TABLE IF EXISTS `receipts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTaskLogs = new HashMap<String, TableInfo.Column>(7);
        _columnsTaskLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("taskId", new TableInfo.Column("taskId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("taskName", new TableInfo.Column("taskName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("section", new TableInfo.Column("section", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskLogs.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTaskLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTaskLogs = new HashSet<TableInfo.Index>(1);
        _indicesTaskLogs.add(new TableInfo.Index("index_task_logs_date_taskId", true, Arrays.asList("date", "taskId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoTaskLogs = new TableInfo("task_logs", _columnsTaskLogs, _foreignKeysTaskLogs, _indicesTaskLogs);
        final TableInfo _existingTaskLogs = TableInfo.read(db, "task_logs");
        if (!_infoTaskLogs.equals(_existingTaskLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "task_logs(com.venkat.healthapp.hair.data.TaskLog).\n"
                  + " Expected:\n" + _infoTaskLogs + "\n"
                  + " Found:\n" + _existingTaskLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsDailySummary = new HashMap<String, TableInfo.Column>(5);
        _columnsDailySummary.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailySummary.put("totalTasks", new TableInfo.Column("totalTasks", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailySummary.put("completedTasks", new TableInfo.Column("completedTasks", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailySummary.put("shampooUsed", new TableInfo.Column("shampooUsed", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailySummary.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailySummary = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailySummary = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailySummary = new TableInfo("daily_summary", _columnsDailySummary, _foreignKeysDailySummary, _indicesDailySummary);
        final TableInfo _existingDailySummary = TableInfo.read(db, "daily_summary");
        if (!_infoDailySummary.equals(_existingDailySummary)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_summary(com.venkat.healthapp.hair.data.DailySummary).\n"
                  + " Expected:\n" + _infoDailySummary + "\n"
                  + " Found:\n" + _existingDailySummary);
        }
        final HashMap<String, TableInfo.Column> _columnsScalpPhotos = new HashMap<String, TableInfo.Column>(8);
        _columnsScalpPhotos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("weekLabel", new TableInfo.Column("weekLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("capturedAt", new TableInfo.Column("capturedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("photoPath1", new TableInfo.Column("photoPath1", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("photoPath2", new TableInfo.Column("photoPath2", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScalpPhotos.put("weekNumber", new TableInfo.Column("weekNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScalpPhotos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScalpPhotos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScalpPhotos = new TableInfo("scalp_photos", _columnsScalpPhotos, _foreignKeysScalpPhotos, _indicesScalpPhotos);
        final TableInfo _existingScalpPhotos = TableInfo.read(db, "scalp_photos");
        if (!_infoScalpPhotos.equals(_existingScalpPhotos)) {
          return new RoomOpenHelper.ValidationResult(false, "scalp_photos(com.venkat.healthapp.hair.data.ScalpPhoto).\n"
                  + " Expected:\n" + _infoScalpPhotos + "\n"
                  + " Found:\n" + _existingScalpPhotos);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodItems = new HashMap<String, TableInfo.Column>(10);
        _columnsFoodItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("fiber", new TableInfo.Column("fiber", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("isCustom", new TableInfo.Column("isCustom", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFoodItems = new TableInfo("food_items", _columnsFoodItems, _foreignKeysFoodItems, _indicesFoodItems);
        final TableInfo _existingFoodItems = TableInfo.read(db, "food_items");
        if (!_infoFoodItems.equals(_existingFoodItems)) {
          return new RoomOpenHelper.ValidationResult(false, "food_items(com.venkat.healthapp.food.data.FoodItem).\n"
                  + " Expected:\n" + _infoFoodItems + "\n"
                  + " Found:\n" + _existingFoodItems);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodLogs = new HashMap<String, TableInfo.Column>(13);
        _columnsFoodLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("foodItemId", new TableInfo.Column("foodItemId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("foodName", new TableInfo.Column("foodName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("fiber", new TableInfo.Column("fiber", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLogs.put("loggedAt", new TableInfo.Column("loggedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFoodLogs = new TableInfo("food_logs", _columnsFoodLogs, _foreignKeysFoodLogs, _indicesFoodLogs);
        final TableInfo _existingFoodLogs = TableInfo.read(db, "food_logs");
        if (!_infoFoodLogs.equals(_existingFoodLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "food_logs(com.venkat.healthapp.food.data.FoodLog).\n"
                  + " Expected:\n" + _infoFoodLogs + "\n"
                  + " Found:\n" + _existingFoodLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsUserProfile = new HashMap<String, TableInfo.Column>(9);
        _columnsUserProfile.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("heightCm", new TableInfo.Column("heightCm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("activityLevel", new TableInfo.Column("activityLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("goal", new TableInfo.Column("goal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("targetWeightKg", new TableInfo.Column("targetWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfile = new TableInfo("user_profile", _columnsUserProfile, _foreignKeysUserProfile, _indicesUserProfile);
        final TableInfo _existingUserProfile = TableInfo.read(db, "user_profile");
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profile(com.venkat.healthapp.food.data.UserProfile).\n"
                  + " Expected:\n" + _infoUserProfile + "\n"
                  + " Found:\n" + _existingUserProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsWaterLogs = new HashMap<String, TableInfo.Column>(4);
        _columnsWaterLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterLogs.put("amountMl", new TableInfo.Column("amountMl", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterLogs.put("loggedAt", new TableInfo.Column("loggedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWaterLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWaterLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWaterLogs = new TableInfo("water_logs", _columnsWaterLogs, _foreignKeysWaterLogs, _indicesWaterLogs);
        final TableInfo _existingWaterLogs = TableInfo.read(db, "water_logs");
        if (!_infoWaterLogs.equals(_existingWaterLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "water_logs(com.venkat.healthapp.water.data.WaterLog).\n"
                  + " Expected:\n" + _infoWaterLogs + "\n"
                  + " Found:\n" + _existingWaterLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsSleepLogs = new HashMap<String, TableInfo.Column>(9);
        _columnsSleepLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("bedTimeMillis", new TableInfo.Column("bedTimeMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("wakeTimeMillis", new TableInfo.Column("wakeTimeMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("quality", new TableInfo.Column("quality", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("deepSleepMinutes", new TableInfo.Column("deepSleepMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepLogs.put("loggedAt", new TableInfo.Column("loggedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSleepLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSleepLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSleepLogs = new TableInfo("sleep_logs", _columnsSleepLogs, _foreignKeysSleepLogs, _indicesSleepLogs);
        final TableInfo _existingSleepLogs = TableInfo.read(db, "sleep_logs");
        if (!_infoSleepLogs.equals(_existingSleepLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "sleep_logs(com.venkat.healthapp.sleep.data.SleepLog).\n"
                  + " Expected:\n" + _infoSleepLogs + "\n"
                  + " Found:\n" + _existingSleepLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutLogs = new HashMap<String, TableInfo.Column>(11);
        _columnsWorkoutLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("splitName", new TableInfo.Column("splitName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("exerciseName", new TableInfo.Column("exerciseName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("sets", new TableInfo.Column("sets", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("reps", new TableInfo.Column("reps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLogs.put("loggedAt", new TableInfo.Column("loggedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkoutLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutLogs = new TableInfo("workout_logs", _columnsWorkoutLogs, _foreignKeysWorkoutLogs, _indicesWorkoutLogs);
        final TableInfo _existingWorkoutLogs = TableInfo.read(db, "workout_logs");
        if (!_infoWorkoutLogs.equals(_existingWorkoutLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_logs(com.venkat.healthapp.workout.data.WorkoutLog).\n"
                  + " Expected:\n" + _infoWorkoutLogs + "\n"
                  + " Found:\n" + _existingWorkoutLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutProgress = new HashMap<String, TableInfo.Column>(7);
        _columnsWorkoutProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("splitCompleted", new TableInfo.Column("splitCompleted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("totalExercises", new TableInfo.Column("totalExercises", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("completedExercises", new TableInfo.Column("completedExercises", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutProgress.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkoutProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutProgress = new TableInfo("workout_progress", _columnsWorkoutProgress, _foreignKeysWorkoutProgress, _indicesWorkoutProgress);
        final TableInfo _existingWorkoutProgress = TableInfo.read(db, "workout_progress");
        if (!_infoWorkoutProgress.equals(_existingWorkoutProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_progress(com.venkat.healthapp.workout.data.WorkoutProgress).\n"
                  + " Expected:\n" + _infoWorkoutProgress + "\n"
                  + " Found:\n" + _existingWorkoutProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(13);
        _columnsExpenses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("paymentMode", new TableInfo.Column("paymentMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("noteReminderSet", new TableInfo.Column("noteReminderSet", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("noteAdded", new TableInfo.Column("noteAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isRecurring", new TableInfo.Column("isRecurring", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(db, "expenses");
        if (!_infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.venkat.healthapp.expense.data.Expense).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenseBudget = new HashMap<String, TableInfo.Column>(3);
        _columnsExpenseBudget.put("category", new TableInfo.Column("category", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseBudget.put("monthlyLimit", new TableInfo.Column("monthlyLimit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseBudget.put("month", new TableInfo.Column("month", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenseBudget = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenseBudget = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenseBudget = new TableInfo("expense_budget", _columnsExpenseBudget, _foreignKeysExpenseBudget, _indicesExpenseBudget);
        final TableInfo _existingExpenseBudget = TableInfo.read(db, "expense_budget");
        if (!_infoExpenseBudget.equals(_existingExpenseBudget)) {
          return new RoomOpenHelper.ValidationResult(false, "expense_budget(com.venkat.healthapp.expense.data.ExpenseBudget).\n"
                  + " Expected:\n" + _infoExpenseBudget + "\n"
                  + " Found:\n" + _existingExpenseBudget);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenseReminder = new HashMap<String, TableInfo.Column>(5);
        _columnsExpenseReminder.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseReminder.put("expenseId", new TableInfo.Column("expenseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseReminder.put("reminderAt", new TableInfo.Column("reminderAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseReminder.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseReminder.put("isDone", new TableInfo.Column("isDone", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenseReminder = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenseReminder = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenseReminder = new TableInfo("expense_reminder", _columnsExpenseReminder, _foreignKeysExpenseReminder, _indicesExpenseReminder);
        final TableInfo _existingExpenseReminder = TableInfo.read(db, "expense_reminder");
        if (!_infoExpenseReminder.equals(_existingExpenseReminder)) {
          return new RoomOpenHelper.ValidationResult(false, "expense_reminder(com.venkat.healthapp.expense.data.ExpenseReminder).\n"
                  + " Expected:\n" + _infoExpenseReminder + "\n"
                  + " Found:\n" + _existingExpenseReminder);
        }
        final HashMap<String, TableInfo.Column> _columnsLendBorrow = new HashMap<String, TableInfo.Column>(13);
        _columnsLendBorrow.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("personName", new TableInfo.Column("personName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("personPhone", new TableInfo.Column("personPhone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("paidBack", new TableInfo.Column("paidBack", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("dueDate", new TableInfo.Column("dueDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("reminderSet", new TableInfo.Column("reminderSet", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLendBorrow.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLendBorrow = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLendBorrow = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLendBorrow = new TableInfo("lend_borrow", _columnsLendBorrow, _foreignKeysLendBorrow, _indicesLendBorrow);
        final TableInfo _existingLendBorrow = TableInfo.read(db, "lend_borrow");
        if (!_infoLendBorrow.equals(_existingLendBorrow)) {
          return new RoomOpenHelper.ValidationResult(false, "lend_borrow(com.venkat.healthapp.expense.data.LendBorrow).\n"
                  + " Expected:\n" + _infoLendBorrow + "\n"
                  + " Found:\n" + _existingLendBorrow);
        }
        final HashMap<String, TableInfo.Column> _columnsPartialPayment = new HashMap<String, TableInfo.Column>(6);
        _columnsPartialPayment.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartialPayment.put("lendBorrowId", new TableInfo.Column("lendBorrowId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartialPayment.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartialPayment.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartialPayment.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartialPayment.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPartialPayment = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPartialPayment = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPartialPayment = new TableInfo("partial_payment", _columnsPartialPayment, _foreignKeysPartialPayment, _indicesPartialPayment);
        final TableInfo _existingPartialPayment = TableInfo.read(db, "partial_payment");
        if (!_infoPartialPayment.equals(_existingPartialPayment)) {
          return new RoomOpenHelper.ValidationResult(false, "partial_payment(com.venkat.healthapp.expense.data.PartialPayment).\n"
                  + " Expected:\n" + _infoPartialPayment + "\n"
                  + " Found:\n" + _existingPartialPayment);
        }
        final HashMap<String, TableInfo.Column> _columnsSplitExpense = new HashMap<String, TableInfo.Column>(8);
        _columnsSplitExpense.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("totalAmount", new TableInfo.Column("totalAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("paidBy", new TableInfo.Column("paidBy", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("isSettled", new TableInfo.Column("isSettled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitExpense.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSplitExpense = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSplitExpense = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSplitExpense = new TableInfo("split_expense", _columnsSplitExpense, _foreignKeysSplitExpense, _indicesSplitExpense);
        final TableInfo _existingSplitExpense = TableInfo.read(db, "split_expense");
        if (!_infoSplitExpense.equals(_existingSplitExpense)) {
          return new RoomOpenHelper.ValidationResult(false, "split_expense(com.venkat.healthapp.expense.data.SplitExpense).\n"
                  + " Expected:\n" + _infoSplitExpense + "\n"
                  + " Found:\n" + _existingSplitExpense);
        }
        final HashMap<String, TableInfo.Column> _columnsSplitMember = new HashMap<String, TableInfo.Column>(7);
        _columnsSplitMember.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("splitExpenseId", new TableInfo.Column("splitExpenseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("shareAmount", new TableInfo.Column("shareAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("isPaid", new TableInfo.Column("isPaid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSplitMember.put("paidAt", new TableInfo.Column("paidAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSplitMember = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSplitMember = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSplitMember = new TableInfo("split_member", _columnsSplitMember, _foreignKeysSplitMember, _indicesSplitMember);
        final TableInfo _existingSplitMember = TableInfo.read(db, "split_member");
        if (!_infoSplitMember.equals(_existingSplitMember)) {
          return new RoomOpenHelper.ValidationResult(false, "split_member(com.venkat.healthapp.expense.data.SplitMember).\n"
                  + " Expected:\n" + _infoSplitMember + "\n"
                  + " Found:\n" + _existingSplitMember);
        }
        final HashMap<String, TableInfo.Column> _columnsVaultItems = new HashMap<String, TableInfo.Column>(11);
        _columnsVaultItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("encryptedPassword", new TableInfo.Column("encryptedPassword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("encryptedExtra", new TableInfo.Column("encryptedExtra", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("website", new TableInfo.Column("website", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultItems.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaultItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVaultItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVaultItems = new TableInfo("vault_items", _columnsVaultItems, _foreignKeysVaultItems, _indicesVaultItems);
        final TableInfo _existingVaultItems = TableInfo.read(db, "vault_items");
        if (!_infoVaultItems.equals(_existingVaultItems)) {
          return new RoomOpenHelper.ValidationResult(false, "vault_items(com.venkat.healthapp.vault.data.VaultItem).\n"
                  + " Expected:\n" + _infoVaultItems + "\n"
                  + " Found:\n" + _existingVaultItems);
        }
        final HashMap<String, TableInfo.Column> _columnsReceipts = new HashMap<String, TableInfo.Column>(10);
        _columnsReceipts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("expenseId", new TableInfo.Column("expenseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("imagePath", new TableInfo.Column("imagePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("ocrText", new TableInfo.Column("ocrText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("detectedAmount", new TableInfo.Column("detectedAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("detectedDate", new TableInfo.Column("detectedDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("detectedMerchant", new TableInfo.Column("detectedMerchant", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("capturedAt", new TableInfo.Column("capturedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceipts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceipts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceipts = new TableInfo("receipts", _columnsReceipts, _foreignKeysReceipts, _indicesReceipts);
        final TableInfo _existingReceipts = TableInfo.read(db, "receipts");
        if (!_infoReceipts.equals(_existingReceipts)) {
          return new RoomOpenHelper.ValidationResult(false, "receipts(com.venkat.healthapp.expense.receipt.Receipt).\n"
                  + " Expected:\n" + _infoReceipts + "\n"
                  + " Found:\n" + _existingReceipts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0412b8f781ff9e30ad42704c0a79ce74", "f0a1ae154e4735148962fda4bcbfd125");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "task_logs","daily_summary","scalp_photos","food_items","food_logs","user_profile","water_logs","sleep_logs","workout_logs","workout_progress","expenses","expense_budget","expense_reminder","lend_borrow","partial_payment","split_expense","split_member","vault_items","receipts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `task_logs`");
      _db.execSQL("DELETE FROM `daily_summary`");
      _db.execSQL("DELETE FROM `scalp_photos`");
      _db.execSQL("DELETE FROM `food_items`");
      _db.execSQL("DELETE FROM `food_logs`");
      _db.execSQL("DELETE FROM `user_profile`");
      _db.execSQL("DELETE FROM `water_logs`");
      _db.execSQL("DELETE FROM `sleep_logs`");
      _db.execSQL("DELETE FROM `workout_logs`");
      _db.execSQL("DELETE FROM `workout_progress`");
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `expense_budget`");
      _db.execSQL("DELETE FROM `expense_reminder`");
      _db.execSQL("DELETE FROM `lend_borrow`");
      _db.execSQL("DELETE FROM `partial_payment`");
      _db.execSQL("DELETE FROM `split_expense`");
      _db.execSQL("DELETE FROM `split_member`");
      _db.execSQL("DELETE FROM `vault_items`");
      _db.execSQL("DELETE FROM `receipts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TaskLogDao.class, TaskLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailySummaryDao.class, DailySummaryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScalpPhotoDao.class, ScalpPhotoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FoodItemDao.class, FoodItemDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FoodLogDao.class, FoodLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserProfileDao.class, UserProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WaterLogDao.class, WaterLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SleepLogDao.class, SleepLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutLogDao.class, WorkoutLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutProgressDao.class, WorkoutProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseBudgetDao.class, ExpenseBudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseReminderDao.class, ExpenseReminderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LendBorrowDao.class, LendBorrowDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PartialPaymentDao.class, PartialPaymentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SplitExpenseDao.class, SplitExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VaultDao.class, VaultDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReceiptDao.class, ReceiptDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TaskLogDao taskLogDao() {
    if (_taskLogDao != null) {
      return _taskLogDao;
    } else {
      synchronized(this) {
        if(_taskLogDao == null) {
          _taskLogDao = new TaskLogDao_Impl(this);
        }
        return _taskLogDao;
      }
    }
  }

  @Override
  public DailySummaryDao dailySummaryDao() {
    if (_dailySummaryDao != null) {
      return _dailySummaryDao;
    } else {
      synchronized(this) {
        if(_dailySummaryDao == null) {
          _dailySummaryDao = new DailySummaryDao_Impl(this);
        }
        return _dailySummaryDao;
      }
    }
  }

  @Override
  public ScalpPhotoDao scalpPhotoDao() {
    if (_scalpPhotoDao != null) {
      return _scalpPhotoDao;
    } else {
      synchronized(this) {
        if(_scalpPhotoDao == null) {
          _scalpPhotoDao = new ScalpPhotoDao_Impl(this);
        }
        return _scalpPhotoDao;
      }
    }
  }

  @Override
  public FoodItemDao foodItemDao() {
    if (_foodItemDao != null) {
      return _foodItemDao;
    } else {
      synchronized(this) {
        if(_foodItemDao == null) {
          _foodItemDao = new FoodItemDao_Impl(this);
        }
        return _foodItemDao;
      }
    }
  }

  @Override
  public FoodLogDao foodLogDao() {
    if (_foodLogDao != null) {
      return _foodLogDao;
    } else {
      synchronized(this) {
        if(_foodLogDao == null) {
          _foodLogDao = new FoodLogDao_Impl(this);
        }
        return _foodLogDao;
      }
    }
  }

  @Override
  public UserProfileDao userProfileDao() {
    if (_userProfileDao != null) {
      return _userProfileDao;
    } else {
      synchronized(this) {
        if(_userProfileDao == null) {
          _userProfileDao = new UserProfileDao_Impl(this);
        }
        return _userProfileDao;
      }
    }
  }

  @Override
  public WaterLogDao waterLogDao() {
    if (_waterLogDao != null) {
      return _waterLogDao;
    } else {
      synchronized(this) {
        if(_waterLogDao == null) {
          _waterLogDao = new WaterLogDao_Impl(this);
        }
        return _waterLogDao;
      }
    }
  }

  @Override
  public SleepLogDao sleepLogDao() {
    if (_sleepLogDao != null) {
      return _sleepLogDao;
    } else {
      synchronized(this) {
        if(_sleepLogDao == null) {
          _sleepLogDao = new SleepLogDao_Impl(this);
        }
        return _sleepLogDao;
      }
    }
  }

  @Override
  public WorkoutLogDao workoutLogDao() {
    if (_workoutLogDao != null) {
      return _workoutLogDao;
    } else {
      synchronized(this) {
        if(_workoutLogDao == null) {
          _workoutLogDao = new WorkoutLogDao_Impl(this);
        }
        return _workoutLogDao;
      }
    }
  }

  @Override
  public WorkoutProgressDao workoutProgressDao() {
    if (_workoutProgressDao != null) {
      return _workoutProgressDao;
    } else {
      synchronized(this) {
        if(_workoutProgressDao == null) {
          _workoutProgressDao = new WorkoutProgressDao_Impl(this);
        }
        return _workoutProgressDao;
      }
    }
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public ExpenseBudgetDao expenseBudgetDao() {
    if (_expenseBudgetDao != null) {
      return _expenseBudgetDao;
    } else {
      synchronized(this) {
        if(_expenseBudgetDao == null) {
          _expenseBudgetDao = new ExpenseBudgetDao_Impl(this);
        }
        return _expenseBudgetDao;
      }
    }
  }

  @Override
  public ExpenseReminderDao expenseReminderDao() {
    if (_expenseReminderDao != null) {
      return _expenseReminderDao;
    } else {
      synchronized(this) {
        if(_expenseReminderDao == null) {
          _expenseReminderDao = new ExpenseReminderDao_Impl(this);
        }
        return _expenseReminderDao;
      }
    }
  }

  @Override
  public LendBorrowDao lendBorrowDao() {
    if (_lendBorrowDao != null) {
      return _lendBorrowDao;
    } else {
      synchronized(this) {
        if(_lendBorrowDao == null) {
          _lendBorrowDao = new LendBorrowDao_Impl(this);
        }
        return _lendBorrowDao;
      }
    }
  }

  @Override
  public PartialPaymentDao partialPaymentDao() {
    if (_partialPaymentDao != null) {
      return _partialPaymentDao;
    } else {
      synchronized(this) {
        if(_partialPaymentDao == null) {
          _partialPaymentDao = new PartialPaymentDao_Impl(this);
        }
        return _partialPaymentDao;
      }
    }
  }

  @Override
  public SplitExpenseDao splitExpenseDao() {
    if (_splitExpenseDao != null) {
      return _splitExpenseDao;
    } else {
      synchronized(this) {
        if(_splitExpenseDao == null) {
          _splitExpenseDao = new SplitExpenseDao_Impl(this);
        }
        return _splitExpenseDao;
      }
    }
  }

  @Override
  public VaultDao vaultDao() {
    if (_vaultDao != null) {
      return _vaultDao;
    } else {
      synchronized(this) {
        if(_vaultDao == null) {
          _vaultDao = new VaultDao_Impl(this);
        }
        return _vaultDao;
      }
    }
  }

  @Override
  public ReceiptDao receiptDao() {
    if (_receiptDao != null) {
      return _receiptDao;
    } else {
      synchronized(this) {
        if(_receiptDao == null) {
          _receiptDao = new ReceiptDao_Impl(this);
        }
        return _receiptDao;
      }
    }
  }
}
