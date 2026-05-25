package com.venkat.healthapp.expense.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class ExpenseBudgetDao_Impl implements ExpenseBudgetDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<ExpenseBudget> __upsertionAdapterOfExpenseBudget;

  public ExpenseBudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfExpenseBudget = new EntityUpsertionAdapter<ExpenseBudget>(new EntityInsertionAdapter<ExpenseBudget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `expense_budget` (`category`,`monthlyLimit`,`month`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseBudget entity) {
        statement.bindString(1, entity.getCategory());
        statement.bindDouble(2, entity.getMonthlyLimit());
        statement.bindString(3, entity.getMonth());
      }
    }, new EntityDeletionOrUpdateAdapter<ExpenseBudget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `expense_budget` SET `category` = ?,`monthlyLimit` = ?,`month` = ? WHERE `category` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseBudget entity) {
        statement.bindString(1, entity.getCategory());
        statement.bindDouble(2, entity.getMonthlyLimit());
        statement.bindString(3, entity.getMonth());
        statement.bindString(4, entity.getCategory());
      }
    });
  }

  @Override
  public Object upsert(final ExpenseBudget budget, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfExpenseBudget.upsert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseBudget>> budgetsForMonth(final String month) {
    final String _sql = "SELECT * FROM expense_budget WHERE month = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, month);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expense_budget"}, new Callable<List<ExpenseBudget>>() {
      @Override
      @NonNull
      public List<ExpenseBudget> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMonthlyLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyLimit");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final List<ExpenseBudget> _result = new ArrayList<ExpenseBudget>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseBudget _item;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final float _tmpMonthlyLimit;
            _tmpMonthlyLimit = _cursor.getFloat(_cursorIndexOfMonthlyLimit);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            _item = new ExpenseBudget(_tmpCategory,_tmpMonthlyLimit,_tmpMonth);
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
