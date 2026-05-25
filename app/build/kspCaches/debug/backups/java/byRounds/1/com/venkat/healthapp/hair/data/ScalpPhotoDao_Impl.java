package com.venkat.healthapp.hair.data;

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
public final class ScalpPhotoDao_Impl implements ScalpPhotoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScalpPhoto> __insertionAdapterOfScalpPhoto;

  private final EntityDeletionOrUpdateAdapter<ScalpPhoto> __deletionAdapterOfScalpPhoto;

  public ScalpPhotoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScalpPhoto = new EntityInsertionAdapter<ScalpPhoto>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scalp_photos` (`id`,`weekLabel`,`date`,`capturedAt`,`photoPath1`,`photoPath2`,`label`,`weekNumber`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScalpPhoto entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getWeekLabel());
        statement.bindString(3, entity.getDate());
        statement.bindLong(4, entity.getCapturedAt());
        statement.bindString(5, entity.getPhotoPath1());
        statement.bindString(6, entity.getPhotoPath2());
        statement.bindString(7, entity.getLabel());
        statement.bindLong(8, entity.getWeekNumber());
      }
    };
    this.__deletionAdapterOfScalpPhoto = new EntityDeletionOrUpdateAdapter<ScalpPhoto>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `scalp_photos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScalpPhoto entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ScalpPhoto p, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScalpPhoto.insertAndReturnId(p);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ScalpPhoto p, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfScalpPhoto.handle(p);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScalpPhoto>> allPhotos() {
    final String _sql = "SELECT * FROM scalp_photos ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scalp_photos"}, new Callable<List<ScalpPhoto>>() {
      @Override
      @NonNull
      public List<ScalpPhoto> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWeekLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "weekLabel");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfPhotoPath1 = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath1");
          final int _cursorIndexOfPhotoPath2 = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath2");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfWeekNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "weekNumber");
          final List<ScalpPhoto> _result = new ArrayList<ScalpPhoto>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScalpPhoto _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpWeekLabel;
            _tmpWeekLabel = _cursor.getString(_cursorIndexOfWeekLabel);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpPhotoPath1;
            _tmpPhotoPath1 = _cursor.getString(_cursorIndexOfPhotoPath1);
            final String _tmpPhotoPath2;
            _tmpPhotoPath2 = _cursor.getString(_cursorIndexOfPhotoPath2);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final int _tmpWeekNumber;
            _tmpWeekNumber = _cursor.getInt(_cursorIndexOfWeekNumber);
            _item = new ScalpPhoto(_tmpId,_tmpWeekLabel,_tmpDate,_tmpCapturedAt,_tmpPhotoPath1,_tmpPhotoPath2,_tmpLabel,_tmpWeekNumber);
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
  public Flow<Integer> totalCount() {
    final String _sql = "SELECT COUNT(*) FROM scalp_photos";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scalp_photos"}, new Callable<Integer>() {
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
  public Flow<ScalpPhoto> latestPhoto() {
    final String _sql = "SELECT * FROM scalp_photos ORDER BY capturedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scalp_photos"}, new Callable<ScalpPhoto>() {
      @Override
      @Nullable
      public ScalpPhoto call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWeekLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "weekLabel");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfPhotoPath1 = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath1");
          final int _cursorIndexOfPhotoPath2 = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath2");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfWeekNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "weekNumber");
          final ScalpPhoto _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpWeekLabel;
            _tmpWeekLabel = _cursor.getString(_cursorIndexOfWeekLabel);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpCapturedAt;
            _tmpCapturedAt = _cursor.getLong(_cursorIndexOfCapturedAt);
            final String _tmpPhotoPath1;
            _tmpPhotoPath1 = _cursor.getString(_cursorIndexOfPhotoPath1);
            final String _tmpPhotoPath2;
            _tmpPhotoPath2 = _cursor.getString(_cursorIndexOfPhotoPath2);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final int _tmpWeekNumber;
            _tmpWeekNumber = _cursor.getInt(_cursorIndexOfWeekNumber);
            _result = new ScalpPhoto(_tmpId,_tmpWeekLabel,_tmpDate,_tmpCapturedAt,_tmpPhotoPath1,_tmpPhotoPath2,_tmpLabel,_tmpWeekNumber);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
