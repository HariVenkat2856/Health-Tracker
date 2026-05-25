package com.venkat.healthapp.hair.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.lang.Integer;
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
public final class DailySummaryDao_Impl implements DailySummaryDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<DailySummary> __upsertionAdapterOfDailySummary;

  public DailySummaryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfDailySummary = new EntityUpsertionAdapter<DailySummary>(new EntityInsertionAdapter<DailySummary>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `daily_summary` (`date`,`totalTasks`,`completedTasks`,`shampooUsed`,`notes`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailySummary entity) {
        statement.bindString(1, entity.getDate());
        statement.bindLong(2, entity.getTotalTasks());
        statement.bindLong(3, entity.getCompletedTasks());
        statement.bindString(4, entity.getShampooUsed());
        statement.bindString(5, entity.getNotes());
      }
    }, new EntityDeletionOrUpdateAdapter<DailySummary>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `daily_summary` SET `date` = ?,`totalTasks` = ?,`completedTasks` = ?,`shampooUsed` = ?,`notes` = ? WHERE `date` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailySummary entity) {
        statement.bindString(1, entity.getDate());
        statement.bindLong(2, entity.getTotalTasks());
        statement.bindLong(3, entity.getCompletedTasks());
        statement.bindString(4, entity.getShampooUsed());
        statement.bindString(5, entity.getNotes());
        statement.bindString(6, entity.getDate());
      }
    });
  }

  @Override
  public Object upsert(final DailySummary s, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDailySummary.upsert(s);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailySummary>> allSummaries() {
    final String _sql = "SELECT * FROM daily_summary ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<List<DailySummary>>() {
      @Override
      @NonNull
      public List<DailySummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTotalTasks = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTasks");
          final int _cursorIndexOfCompletedTasks = CursorUtil.getColumnIndexOrThrow(_cursor, "completedTasks");
          final int _cursorIndexOfShampooUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "shampooUsed");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DailySummary> _result = new ArrayList<DailySummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailySummary _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpTotalTasks;
            _tmpTotalTasks = _cursor.getInt(_cursorIndexOfTotalTasks);
            final int _tmpCompletedTasks;
            _tmpCompletedTasks = _cursor.getInt(_cursorIndexOfCompletedTasks);
            final String _tmpShampooUsed;
            _tmpShampooUsed = _cursor.getString(_cursorIndexOfShampooUsed);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new DailySummary(_tmpDate,_tmpTotalTasks,_tmpCompletedTasks,_tmpShampooUsed,_tmpNotes);
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

  @Override
  public Flow<DailySummary> summaryForDate(final String date) {
    final String _sql = "SELECT * FROM daily_summary WHERE date=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<DailySummary>() {
      @Override
      @Nullable
      public DailySummary call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTotalTasks = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTasks");
          final int _cursorIndexOfCompletedTasks = CursorUtil.getColumnIndexOrThrow(_cursor, "completedTasks");
          final int _cursorIndexOfShampooUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "shampooUsed");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final DailySummary _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpTotalTasks;
            _tmpTotalTasks = _cursor.getInt(_cursorIndexOfTotalTasks);
            final int _tmpCompletedTasks;
            _tmpCompletedTasks = _cursor.getInt(_cursorIndexOfCompletedTasks);
            final String _tmpShampooUsed;
            _tmpShampooUsed = _cursor.getString(_cursorIndexOfShampooUsed);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new DailySummary(_tmpDate,_tmpTotalTasks,_tmpCompletedTasks,_tmpShampooUsed,_tmpNotes);
          } else {
            _result = null;
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

  @Override
  public Flow<Integer> perfectDaysCount() {
    final String _sql = "SELECT COUNT(*) FROM daily_summary WHERE completedTasks=totalTasks AND totalTasks>0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  @Override
  public Flow<Integer> activeDaysCount() {
    final String _sql = "SELECT COUNT(*) FROM daily_summary WHERE completedTasks>0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  @Override
  public Flow<Integer> totalCompletedAllTime() {
    final String _sql = "SELECT SUM(completedTasks) FROM daily_summary";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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

  @Override
  public Flow<List<String>> datesWithActivity() {
    final String _sql = "SELECT date FROM daily_summary WHERE completedTasks>0 ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_summary"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
