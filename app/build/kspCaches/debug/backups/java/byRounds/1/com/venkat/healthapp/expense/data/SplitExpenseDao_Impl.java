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
public final class SplitExpenseDao_Impl implements SplitExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SplitExpense> __insertionAdapterOfSplitExpense;

  private final EntityInsertionAdapter<SplitMember> __insertionAdapterOfSplitMember;

  private final EntityDeletionOrUpdateAdapter<SplitExpense> __deletionAdapterOfSplitExpense;

  private final EntityDeletionOrUpdateAdapter<SplitExpense> __updateAdapterOfSplitExpense;

  private final EntityDeletionOrUpdateAdapter<SplitMember> __updateAdapterOfSplitMember;

  public SplitExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSplitExpense = new EntityInsertionAdapter<SplitExpense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `split_expense` (`id`,`title`,`totalAmount`,`date`,`paidBy`,`note`,`isSettled`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SplitExpense entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindDouble(3, entity.getTotalAmount());
        statement.bindString(4, entity.getDate());
        statement.bindString(5, entity.getPaidBy());
        statement.bindString(6, entity.getNote());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfSplitMember = new EntityInsertionAdapter<SplitMember>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `split_member` (`id`,`splitExpenseId`,`name`,`phone`,`shareAmount`,`isPaid`,`paidAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SplitMember entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSplitExpenseId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getPhone());
        statement.bindDouble(5, entity.getShareAmount());
        final int _tmp = entity.isPaid() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getPaidAt());
      }
    };
    this.__deletionAdapterOfSplitExpense = new EntityDeletionOrUpdateAdapter<SplitExpense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `split_expense` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SplitExpense entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSplitExpense = new EntityDeletionOrUpdateAdapter<SplitExpense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `split_expense` SET `id` = ?,`title` = ?,`totalAmount` = ?,`date` = ?,`paidBy` = ?,`note` = ?,`isSettled` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SplitExpense entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindDouble(3, entity.getTotalAmount());
        statement.bindString(4, entity.getDate());
        statement.bindString(5, entity.getPaidBy());
        statement.bindString(6, entity.getNote());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__updateAdapterOfSplitMember = new EntityDeletionOrUpdateAdapter<SplitMember>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `split_member` SET `id` = ?,`splitExpenseId` = ?,`name` = ?,`phone` = ?,`shareAmount` = ?,`isPaid` = ?,`paidAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SplitMember entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSplitExpenseId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getPhone());
        statement.bindDouble(5, entity.getShareAmount());
        final int _tmp = entity.isPaid() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getPaidAt());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertSplit(final SplitExpense s, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSplitExpense.insertAndReturnId(s);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMember(final SplitMember m, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSplitMember.insertAndReturnId(m);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSplit(final SplitExpense s, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSplitExpense.handle(s);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSplit(final SplitExpense s, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSplitExpense.handle(s);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMember(final SplitMember m, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSplitMember.handle(m);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SplitExpense>> allSplits() {
    final String _sql = "SELECT * FROM split_expense ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"split_expense"}, new Callable<List<SplitExpense>>() {
      @Override
      @NonNull
      public List<SplitExpense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaidBy = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBy");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "isSettled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SplitExpense> _result = new ArrayList<SplitExpense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SplitExpense _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final float _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getFloat(_cursorIndexOfTotalAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaidBy;
            _tmpPaidBy = _cursor.getString(_cursorIndexOfPaidBy);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SplitExpense(_tmpId,_tmpTitle,_tmpTotalAmount,_tmpDate,_tmpPaidBy,_tmpNote,_tmpIsSettled,_tmpCreatedAt);
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
  public Flow<List<SplitMember>> membersForSplit(final int splitId) {
    final String _sql = "SELECT * FROM split_member WHERE splitExpenseId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, splitId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"split_member"}, new Callable<List<SplitMember>>() {
      @Override
      @NonNull
      public List<SplitMember> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSplitExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "splitExpenseId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfShareAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "shareAmount");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaid");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final List<SplitMember> _result = new ArrayList<SplitMember>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SplitMember _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSplitExpenseId;
            _tmpSplitExpenseId = _cursor.getInt(_cursorIndexOfSplitExpenseId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final float _tmpShareAmount;
            _tmpShareAmount = _cursor.getFloat(_cursorIndexOfShareAmount);
            final boolean _tmpIsPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp != 0;
            final long _tmpPaidAt;
            _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
            _item = new SplitMember(_tmpId,_tmpSplitExpenseId,_tmpName,_tmpPhone,_tmpShareAmount,_tmpIsPaid,_tmpPaidAt);
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
  public Object getSplitById(final int id, final Continuation<? super SplitExpense> $completion) {
    final String _sql = "SELECT * FROM split_expense WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SplitExpense>() {
      @Override
      @Nullable
      public SplitExpense call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaidBy = CursorUtil.getColumnIndexOrThrow(_cursor, "paidBy");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "isSettled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final SplitExpense _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final float _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getFloat(_cursorIndexOfTotalAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaidBy;
            _tmpPaidBy = _cursor.getString(_cursorIndexOfPaidBy);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new SplitExpense(_tmpId,_tmpTitle,_tmpTotalAmount,_tmpDate,_tmpPaidBy,_tmpNote,_tmpIsSettled,_tmpCreatedAt);
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

  @Override
  public Object getMembersForSplit(final int id,
      final Continuation<? super List<SplitMember>> $completion) {
    final String _sql = "SELECT * FROM split_member WHERE splitExpenseId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SplitMember>>() {
      @Override
      @NonNull
      public List<SplitMember> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSplitExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "splitExpenseId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfShareAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "shareAmount");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaid");
          final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAt");
          final List<SplitMember> _result = new ArrayList<SplitMember>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SplitMember _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSplitExpenseId;
            _tmpSplitExpenseId = _cursor.getInt(_cursorIndexOfSplitExpenseId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final float _tmpShareAmount;
            _tmpShareAmount = _cursor.getFloat(_cursorIndexOfShareAmount);
            final boolean _tmpIsPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp != 0;
            final long _tmpPaidAt;
            _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
            _item = new SplitMember(_tmpId,_tmpSplitExpenseId,_tmpName,_tmpPhone,_tmpShareAmount,_tmpIsPaid,_tmpPaidAt);
            _result.add(_item);
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
