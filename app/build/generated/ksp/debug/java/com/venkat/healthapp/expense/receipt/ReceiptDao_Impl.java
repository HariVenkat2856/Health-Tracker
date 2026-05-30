package com.venkat.healthapp.expense.receipt;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
public final class ReceiptDao_Impl implements ReceiptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Receipt> __insertionAdapterOfReceipt;

  private final EntityDeletionOrUpdateAdapter<Receipt> __deletionAdapterOfReceipt;

  private final EntityDeletionOrUpdateAdapter<Receipt> __updateAdapterOfReceipt;

  public ReceiptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceipt = new EntityInsertionAdapter<Receipt>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `receipts` (`id`,`expenseId`,`imagePath`,`ocrText`,`detectedAmount`,`detectedDate`,`detectedMerchant`,`note`,`capturedAt`,`date`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Receipt entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getExpenseId());
        statement.bindString(3, entity.getImagePath());
        statement.bindString(4, entity.getOcrText());
        statement.bindDouble(5, entity.getDetectedAmount());
        statement.bindString(6, entity.getDetectedDate());
        statement.bindString(7, entity.getDetectedMerchant());
        statement.bindString(8, entity.getNote());
        statement.bindLong(9, entity.getCapturedAt());
        statement.bindString(10, entity.getDate());
      }
    };
    this.__deletionAdapterOfReceipt = new EntityDeletionOrUpdateAdapter<Receipt>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `receipts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Receipt entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReceipt = new EntityDeletionOrUpdateAdapter<Receipt>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `receipts` SET `id` = ?,`expenseId` = ?,`imagePath` = ?,`ocrText` = ?,`detectedAmount` = ?,`detectedDate` = ?,`detectedMerchant` = ?,`note` = ?,`capturedAt` = ?,`date` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Receipt entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getExpenseId());
        statement.bindString(3, entity.getImagePath());
        statement.bindString(4, entity.getOcrText());
        statement.bindDouble(5, entity.getDetectedAmount());
        statement.bindString(6, entity.getDetectedDate());
        statement.bindString(7, entity.getDetectedMerchant());
        statement.bindString(8, entity.getNote());
        statement.bindLong(9, entity.getCapturedAt());
        statement.bindString(10, entity.getDate());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Receipt receipt, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReceipt.insertAndReturnId(receipt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Receipt receipt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReceipt.handle(receipt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Receipt receipt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReceipt.handle(receipt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Receipt>> allReceipts() {
    final String _sql = "SELECT * FROM receipts ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<Receipt>>() {
      @Override
      @NonNull
      public List<Receipt> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfDetectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedAmount");
          final int _cursorIndexOfDetectedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedDate");
          final int _cursorIndexOfDetectedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedMerchant");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Receipt _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpOcrText;
            _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            final float _tmpDetectedAmount;
            _tmpDetectedAmount = _cursor.getFloat(_cursorIndexOfDetectedAmount);
            final String _tmpDetectedDate;
            _tmpDetectedDate = _cursor.getString(_cursorIndexOfDetectedDate);
            final String _tmpDetectedMerchant;
            _tmpDetectedMerchant = _cursor.getString(_cursorIndexOfDetectedMerchant);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            _item = new Receipt(_tmpId,_tmpExpenseId,_tmpImagePath,_tmpOcrText,_tmpDetectedAmount,_tmpDetectedDate,_tmpDetectedMerchant,_tmpNote,_tmpCapturedAt,_tmpDate);
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
  public Flow<List<Receipt>> receiptsForExpense(final int expenseId) {
    final String _sql = "SELECT * FROM receipts WHERE expenseId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, expenseId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<Receipt>>() {
      @Override
      @NonNull
      public List<Receipt> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfDetectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedAmount");
          final int _cursorIndexOfDetectedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedDate");
          final int _cursorIndexOfDetectedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedMerchant");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Receipt _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpOcrText;
            _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            final float _tmpDetectedAmount;
            _tmpDetectedAmount = _cursor.getFloat(_cursorIndexOfDetectedAmount);
            final String _tmpDetectedDate;
            _tmpDetectedDate = _cursor.getString(_cursorIndexOfDetectedDate);
            final String _tmpDetectedMerchant;
            _tmpDetectedMerchant = _cursor.getString(_cursorIndexOfDetectedMerchant);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            _item = new Receipt(_tmpId,_tmpExpenseId,_tmpImagePath,_tmpOcrText,_tmpDetectedAmount,_tmpDetectedDate,_tmpDetectedMerchant,_tmpNote,_tmpCapturedAt,_tmpDate);
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
  public Flow<List<Receipt>> receiptsForDate(final String date) {
    final String _sql = "SELECT * FROM receipts WHERE date = ? ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<Receipt>>() {
      @Override
      @NonNull
      public List<Receipt> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfDetectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedAmount");
          final int _cursorIndexOfDetectedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedDate");
          final int _cursorIndexOfDetectedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedMerchant");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Receipt _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpOcrText;
            _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            final float _tmpDetectedAmount;
            _tmpDetectedAmount = _cursor.getFloat(_cursorIndexOfDetectedAmount);
            final String _tmpDetectedDate;
            _tmpDetectedDate = _cursor.getString(_cursorIndexOfDetectedDate);
            final String _tmpDetectedMerchant;
            _tmpDetectedMerchant = _cursor.getString(_cursorIndexOfDetectedMerchant);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            _item = new Receipt(_tmpId,_tmpExpenseId,_tmpImagePath,_tmpOcrText,_tmpDetectedAmount,_tmpDetectedDate,_tmpDetectedMerchant,_tmpNote,_tmpCapturedAt,_tmpDate);
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
  public Flow<List<Receipt>> standaloneReceipts() {
    final String _sql = "SELECT * FROM receipts WHERE expenseId = 0 ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<Receipt>>() {
      @Override
      @NonNull
      public List<Receipt> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfDetectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedAmount");
          final int _cursorIndexOfDetectedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedDate");
          final int _cursorIndexOfDetectedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedMerchant");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Receipt _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpOcrText;
            _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            final float _tmpDetectedAmount;
            _tmpDetectedAmount = _cursor.getFloat(_cursorIndexOfDetectedAmount);
            final String _tmpDetectedDate;
            _tmpDetectedDate = _cursor.getString(_cursorIndexOfDetectedDate);
            final String _tmpDetectedMerchant;
            _tmpDetectedMerchant = _cursor.getString(_cursorIndexOfDetectedMerchant);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            _item = new Receipt(_tmpId,_tmpExpenseId,_tmpImagePath,_tmpOcrText,_tmpDetectedAmount,_tmpDetectedDate,_tmpDetectedMerchant,_tmpNote,_tmpCapturedAt,_tmpDate);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM receipts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
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
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Receipt>> search(final String q) {
    final String _sql = "SELECT * FROM receipts WHERE ocrText LIKE '%' || ? || '%' OR detectedMerchant LIKE '%' || ? || '%' ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, q);
    _argIndex = 2;
    _statement.bindString(_argIndex, q);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<Receipt>>() {
      @Override
      @NonNull
      public List<Receipt> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "expenseId");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfDetectedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedAmount");
          final int _cursorIndexOfDetectedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedDate");
          final int _cursorIndexOfDetectedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedMerchant");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Receipt _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpExpenseId;
            _tmpExpenseId = _cursor.getInt(_cursorIndexOfExpenseId);
            final String _tmpImagePath;
            _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            final String _tmpOcrText;
            _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            final float _tmpDetectedAmount;
            _tmpDetectedAmount = _cursor.getFloat(_cursorIndexOfDetectedAmount);
            final String _tmpDetectedDate;
            _tmpDetectedDate = _cursor.getString(_cursorIndexOfDetectedDate);
            final String _tmpDetectedMerchant;
            _tmpDetectedMerchant = _cursor.getString(_cursorIndexOfDetectedMerchant);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            _item = new Receipt(_tmpId,_tmpExpenseId,_tmpImagePath,_tmpOcrText,_tmpDetectedAmount,_tmpDetectedDate,_tmpDetectedMerchant,_tmpNote,_tmpCapturedAt,_tmpDate);
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
