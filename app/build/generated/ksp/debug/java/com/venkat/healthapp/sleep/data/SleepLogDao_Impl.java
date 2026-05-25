package com.venkat.healthapp.sleep.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
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
public final class SleepLogDao_Impl implements SleepLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SleepLog> __insertionAdapterOfSleepLog;

  private final EntityDeletionOrUpdateAdapter<SleepLog> __deletionAdapterOfSleepLog;

  public SleepLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSleepLog = new EntityInsertionAdapter<SleepLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sleep_logs` (`id`,`date`,`bedTimeMillis`,`wakeTimeMillis`,`durationMinutes`,`quality`,`note`,`deepSleepMinutes`,`loggedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindLong(3, entity.getBedTimeMillis());
        statement.bindLong(4, entity.getWakeTimeMillis());
        statement.bindLong(5, entity.getDurationMinutes());
        statement.bindLong(6, entity.getQuality());
        statement.bindString(7, entity.getNote());
        statement.bindLong(8, entity.getDeepSleepMinutes());
        statement.bindLong(9, entity.getLoggedAt());
      }
    };
    this.__deletionAdapterOfSleepLog = new EntityDeletionOrUpdateAdapter<SleepLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sleep_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepLog entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SleepLog log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSleepLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SleepLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSleepLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SleepLog>> allLogs() {
    final String _sql = "SELECT * FROM sleep_logs ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<List<SleepLog>>() {
      @Override
      @NonNull
      public List<SleepLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfBedTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "bedTimeMillis");
          final int _cursorIndexOfWakeTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "wakeTimeMillis");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfDeepSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepMinutes");
          final int _cursorIndexOfLoggedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAt");
          final List<SleepLog> _result = new ArrayList<SleepLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepLog _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpBedTimeMillis;
            _tmpBedTimeMillis = _cursor.getLong(_cursorIndexOfBedTimeMillis);
            final long _tmpWakeTimeMillis;
            _tmpWakeTimeMillis = _cursor.getLong(_cursorIndexOfWakeTimeMillis);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final int _tmpDeepSleepMinutes;
            _tmpDeepSleepMinutes = _cursor.getInt(_cursorIndexOfDeepSleepMinutes);
            final long _tmpLoggedAt;
            _tmpLoggedAt = _cursor.getLong(_cursorIndexOfLoggedAt);
            _item = new SleepLog(_tmpId,_tmpDate,_tmpBedTimeMillis,_tmpWakeTimeMillis,_tmpDurationMinutes,_tmpQuality,_tmpNote,_tmpDeepSleepMinutes,_tmpLoggedAt);
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
  public Flow<SleepLog> logForDate(final String date) {
    final String _sql = "SELECT * FROM sleep_logs WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<SleepLog>() {
      @Override
      @Nullable
      public SleepLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfBedTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "bedTimeMillis");
          final int _cursorIndexOfWakeTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "wakeTimeMillis");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfDeepSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepMinutes");
          final int _cursorIndexOfLoggedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAt");
          final SleepLog _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpBedTimeMillis;
            _tmpBedTimeMillis = _cursor.getLong(_cursorIndexOfBedTimeMillis);
            final long _tmpWakeTimeMillis;
            _tmpWakeTimeMillis = _cursor.getLong(_cursorIndexOfWakeTimeMillis);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final int _tmpDeepSleepMinutes;
            _tmpDeepSleepMinutes = _cursor.getInt(_cursorIndexOfDeepSleepMinutes);
            final long _tmpLoggedAt;
            _tmpLoggedAt = _cursor.getLong(_cursorIndexOfLoggedAt);
            _result = new SleepLog(_tmpId,_tmpDate,_tmpBedTimeMillis,_tmpWakeTimeMillis,_tmpDurationMinutes,_tmpQuality,_tmpNote,_tmpDeepSleepMinutes,_tmpLoggedAt);
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
  public Flow<List<SleepLog>> lastSevenDays() {
    final String _sql = "SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 7";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<List<SleepLog>>() {
      @Override
      @NonNull
      public List<SleepLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfBedTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "bedTimeMillis");
          final int _cursorIndexOfWakeTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "wakeTimeMillis");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfDeepSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepMinutes");
          final int _cursorIndexOfLoggedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAt");
          final List<SleepLog> _result = new ArrayList<SleepLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepLog _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpBedTimeMillis;
            _tmpBedTimeMillis = _cursor.getLong(_cursorIndexOfBedTimeMillis);
            final long _tmpWakeTimeMillis;
            _tmpWakeTimeMillis = _cursor.getLong(_cursorIndexOfWakeTimeMillis);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final int _tmpDeepSleepMinutes;
            _tmpDeepSleepMinutes = _cursor.getInt(_cursorIndexOfDeepSleepMinutes);
            final long _tmpLoggedAt;
            _tmpLoggedAt = _cursor.getLong(_cursorIndexOfLoggedAt);
            _item = new SleepLog(_tmpId,_tmpDate,_tmpBedTimeMillis,_tmpWakeTimeMillis,_tmpDurationMinutes,_tmpQuality,_tmpNote,_tmpDeepSleepMinutes,_tmpLoggedAt);
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
  public Flow<Float> avgDurationMinutes() {
    final String _sql = "SELECT AVG(durationMinutes) FROM sleep_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
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
  public Flow<Float> avgQuality() {
    final String _sql = "SELECT AVG(quality) FROM sleep_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
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
  public Flow<Integer> goodSleepDaysCount() {
    final String _sql = "SELECT COUNT(*) FROM sleep_logs WHERE durationMinutes >= 420";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_logs"}, new Callable<Integer>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
