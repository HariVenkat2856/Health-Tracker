package com.venkat.healthapp.expense.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExpenseReminderDao_Impl implements ExpenseReminderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExpenseReminder> __insertionAdapterOfExpenseReminder;

  private final EntityDeletionOrUpdateAdapter<ExpenseReminder> __updateAdapterOfExpenseReminder;

  private final SharedSQLiteStatement __preparedStmtOfMarkDone;

  public ExpenseReminderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpenseReminder = new EntityInsertionAdapter<ExpenseReminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expense_reminder` (`id`,`expenseId`,`reminderAt`,`message`,`isDone`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseReminder entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getExpenseId());
        statement.bindLong(3, entity.getReminderAt());
        statement.bindString(4, entity.getMessage());
        final int _tmp = entity.isDone() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__updateAdapterOfExpenseReminder = new EntityDeletionOrUpdateAdapter<ExpenseReminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `expense_reminder` SET `id` = ?,`expenseId` = ?,`reminderAt` = ?,`message` = ?,`isDone` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseReminder entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getExpenseId());
        statement.bindLong(3, entity.getReminderAt());
        statement.bindString(4, entity.getMessage());
        final int _tmp = entity.isDone() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfMarkDone = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expense_reminder SET isDone = 1 WHERE expenseId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExpenseReminder reminder,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExpenseReminder.insertAndReturnId(reminder);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ExpenseReminder reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExpenseReminder.handle(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markDone(final int expenseId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDone.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, expenseId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkDone.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseReminder>> pendingReminders() {
    final String _sql = "SELECT * FROM expense_reminder WHERE isDone = 0 ORDER BY reminderAt";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expense_reminder"}, new Callable<List<ExpenseReminder>>() {
      @Override
      @NonNull
      public List<ExpenseReminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfReminderAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderAt");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfIsDone = CursorUtil.getColumnIndexOrThrow(_cursor, "isDone");
          final List<ExpenseReminder> _result = new ArrayList<ExpenseReminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseReminder _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final long _tmpReminderAt;
            _tmpReminderAt = _cursor.getLong(_cursorIndexOfReminderAt);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final boolean _tmpIsDone;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDone);
            _tmpIsDone = _tmp != 0;
            _item = new ExpenseReminder(_tmpId,_tmpExpenseId,_tmpReminderAt,_tmpMessage,_tmpIsDone);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
