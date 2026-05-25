package com.venkat.healthapp.expense.data;

import android.database.Cursor;
import android.os.CancellationSignal;
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
public final class LendBorrowDao_Impl implements LendBorrowDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LendBorrow> __insertionAdapterOfLendBorrow;

  private final EntityDeletionOrUpdateAdapter<LendBorrow> __deletionAdapterOfLendBorrow;

  private final EntityDeletionOrUpdateAdapter<LendBorrow> __updateAdapterOfLendBorrow;

  public LendBorrowDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLendBorrow = new EntityInsertionAdapter<LendBorrow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lend_borrow` (`id`,`personName`,`personPhone`,`amount`,`paidBack`,`type`,`status`,`reason`,`date`,`dueDate`,`reminderSet`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LendBorrow entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPersonName());
        statement.bindString(3, entity.getPersonPhone());
        statement.bindDouble(4, entity.getAmount());
        statement.bindDouble(5, entity.getPaidBack());
        statement.bindString(6, entity.getType());
        statement.bindString(7, entity.getStatus());
        statement.bindString(8, entity.getReason());
        statement.bindString(9, entity.getDate());
        statement.bindString(10, entity.getDueDate());
        final int _tmp = entity.getReminderSet() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfLendBorrow = new EntityDeletionOrUpdateAdapter<LendBorrow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `lend_borrow` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LendBorrow entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfLendBorrow = new EntityDeletionOrUpdateAdapter<LendBorrow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `lend_borrow` SET `id` = ?,`personName` = ?,`personPhone` = ?,`amount` = ?,`paidBack` = ?,`type` = ?,`status` = ?,`reason` = ?,`date` = ?,`dueDate` = ?,`reminderSet` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LendBorrow entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPersonName());
        statement.bindString(3, entity.getPersonPhone());
        statement.bindDouble(4, entity.getAmount());
        statement.bindDouble(5, entity.getPaidBack());
        statement.bindString(6, entity.getType());
        statement.bindString(7, entity.getStatus());
        statement.bindString(8, entity.getReason());
        statement.bindString(9, entity.getDate());
        statement.bindString(10, entity.getDueDate());
        final int _tmp = entity.getReminderSet() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getUpdatedAt());
        statement.bindLong(14, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final LendBorrow entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfLendBorrow.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final LendBorrow entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfLendBorrow.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final LendBorrow entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfLendBorrow.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LendBorrow>> all() {
    final String _sql = "SELECT * FROM lend_borrow ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<List<LendBorrow>>() {
      @Override
      @NonNull
      public List<LendBorrow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "personName");
          final int _cursorIndexOfPersonPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "personPhone");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaidBack = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBack");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfReminderSet = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderSet");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LendBorrow> _result = new ArrayList<LendBorrow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LendBorrow _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final String _tmpPersonPhone;
            _tmpPersonPhone = _cursor.getString(_cursorIndexOfPersonPhone);
            final float _tmpAmount;
            _tmpAmount = _cursor.getFloat(_cursorIndexOfAmount);
            final float _tmpPaidBack;
            _tmpPaidBack = _cursor.getFloat(_cursorIndexOfPaidBack);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final boolean _tmpReminderSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderSet);
            _tmpReminderSet = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LendBorrow(_tmpId,_tmpPersonName,_tmpPersonPhone,_tmpAmount,_tmpPaidBack,_tmpType,_tmpStatus,_tmpReason,_tmpDate,_tmpDueDate,_tmpReminderSet,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<LendBorrow>> pendingLent() {
    final String _sql = "SELECT * FROM lend_borrow WHERE type = 'LENT' AND status != 'SETTLED' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<List<LendBorrow>>() {
      @Override
      @NonNull
      public List<LendBorrow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "personName");
          final int _cursorIndexOfPersonPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "personPhone");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaidBack = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBack");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfReminderSet = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderSet");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LendBorrow> _result = new ArrayList<LendBorrow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LendBorrow _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final String _tmpPersonPhone;
            _tmpPersonPhone = _cursor.getString(_cursorIndexOfPersonPhone);
            final float _tmpAmount;
            _tmpAmount = _cursor.getFloat(_cursorIndexOfAmount);
            final float _tmpPaidBack;
            _tmpPaidBack = _cursor.getFloat(_cursorIndexOfPaidBack);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final boolean _tmpReminderSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderSet);
            _tmpReminderSet = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LendBorrow(_tmpId,_tmpPersonName,_tmpPersonPhone,_tmpAmount,_tmpPaidBack,_tmpType,_tmpStatus,_tmpReason,_tmpDate,_tmpDueDate,_tmpReminderSet,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<LendBorrow>> pendingBorrowed() {
    final String _sql = "SELECT * FROM lend_borrow WHERE type = 'BORROWED' AND status != 'SETTLED' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<List<LendBorrow>>() {
      @Override
      @NonNull
      public List<LendBorrow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "personName");
          final int _cursorIndexOfPersonPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "personPhone");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaidBack = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBack");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfReminderSet = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderSet");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LendBorrow> _result = new ArrayList<LendBorrow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LendBorrow _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final String _tmpPersonPhone;
            _tmpPersonPhone = _cursor.getString(_cursorIndexOfPersonPhone);
            final float _tmpAmount;
            _tmpAmount = _cursor.getFloat(_cursorIndexOfAmount);
            final float _tmpPaidBack;
            _tmpPaidBack = _cursor.getFloat(_cursorIndexOfPaidBack);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final boolean _tmpReminderSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderSet);
            _tmpReminderSet = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LendBorrow(_tmpId,_tmpPersonName,_tmpPersonPhone,_tmpAmount,_tmpPaidBack,_tmpType,_tmpStatus,_tmpReason,_tmpDate,_tmpDueDate,_tmpReminderSet,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<LendBorrow>> settled() {
    final String _sql = "SELECT * FROM lend_borrow WHERE status = 'SETTLED' ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<List<LendBorrow>>() {
      @Override
      @NonNull
      public List<LendBorrow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "personName");
          final int _cursorIndexOfPersonPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "personPhone");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaidBack = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBack");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfReminderSet = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderSet");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LendBorrow> _result = new ArrayList<LendBorrow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LendBorrow _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final String _tmpPersonPhone;
            _tmpPersonPhone = _cursor.getString(_cursorIndexOfPersonPhone);
            final float _tmpAmount;
            _tmpAmount = _cursor.getFloat(_cursorIndexOfAmount);
            final float _tmpPaidBack;
            _tmpPaidBack = _cursor.getFloat(_cursorIndexOfPaidBack);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final boolean _tmpReminderSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderSet);
            _tmpReminderSet = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LendBorrow(_tmpId,_tmpPersonName,_tmpPersonPhone,_tmpAmount,_tmpPaidBack,_tmpType,_tmpStatus,_tmpReason,_tmpDate,_tmpDueDate,_tmpReminderSet,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Float> totalLentPending() {
    final String _sql = "SELECT SUM(amount - paidBack) FROM lend_borrow WHERE type = 'LENT' AND status != 'SETTLED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<Float>() {
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
  public Flow<Float> totalBorrowedPending() {
    final String _sql = "SELECT SUM(amount - paidBack) FROM lend_borrow WHERE type = 'BORROWED' AND status != 'SETTLED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lend_borrow"}, new Callable<Float>() {
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
  public Object getById(final int id, final Continuation<? super LendBorrow> $completion) {
    final String _sql = "SELECT * FROM lend_borrow WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LendBorrow>() {
      @Override
      @Nullable
      public LendBorrow call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "personName");
          final int _cursorIndexOfPersonPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "personPhone");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaidBack = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBack");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfReminderSet = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderSet");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final LendBorrow _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final String _tmpPersonPhone;
            _tmpPersonPhone = _cursor.getString(_cursorIndexOfPersonPhone);
            final float _tmpAmount;
            _tmpAmount = _cursor.getFloat(_cursorIndexOfAmount);
            final float _tmpPaidBack;
            _tmpPaidBack = _cursor.getFloat(_cursorIndexOfPaidBack);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpDueDate;
            _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            final boolean _tmpReminderSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderSet);
            _tmpReminderSet = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new LendBorrow(_tmpId,_tmpPersonName,_tmpPersonPhone,_tmpAmount,_tmpPaidBack,_tmpType,_tmpStatus,_tmpReason,_tmpDate,_tmpDueDate,_tmpReminderSet,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
