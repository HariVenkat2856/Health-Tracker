package com.venkat.healthapp.workout.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
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
public final class WorkoutProgressDao_Impl implements WorkoutProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutProgress> __insertionAdapterOfWorkoutProgress;

  public WorkoutProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutProgress = new EntityInsertionAdapter<WorkoutProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workout_progress` (`id`,`date`,`splitCompleted`,`totalExercises`,`completedExercises`,`durationMinutes`,`caloriesBurned`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutProgress entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getSplitCompleted());
        statement.bindLong(4, entity.getTotalExercises());
        statement.bindLong(5, entity.getCompletedExercises());
        statement.bindLong(6, entity.getDurationMinutes());
        statement.bindLong(7, entity.getCaloriesBurned());
      }
    };
  }

  @Override
  public Object insert(final WorkoutProgress p, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutProgress.insert(p);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WorkoutProgress>> allProgress() {
    final String _sql = "SELECT * FROM workout_progress ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_progress"}, new Callable<List<WorkoutProgress>>() {
      @Override
      @NonNull
      public List<WorkoutProgress> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfSplitCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "splitCompleted");
          final int _cursorIndexOfTotalExercises = CursorUtil.getColumnIndexOrThrow(_cursor, "totalExercises");
          final int _cursorIndexOfCompletedExercises = CursorUtil.getColumnIndexOrThrow(_cursor, "completedExercises");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final List<WorkoutProgress> _result = new ArrayList<WorkoutProgress>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutProgress _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpSplitCompleted;
            _tmpSplitCompleted = _cursor.getString(_cursorIndexOfSplitCompleted);
            final int _tmpTotalExercises;
            _tmpTotalExercises = _cursor.getInt(_cursorIndexOfTotalExercises);
            final int _tmpCompletedExercises;
            _tmpCompletedExercises = _cursor.getInt(_cursorIndexOfCompletedExercises);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            _item = new WorkoutProgress(_tmpId,_tmpDate,_tmpSplitCompleted,_tmpTotalExercises,_tmpCompletedExercises,_tmpDurationMinutes,_tmpCaloriesBurned);
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
  public Flow<List<WorkoutProgress>> lastSevenDays() {
    final String _sql = "SELECT * FROM workout_progress ORDER BY date DESC LIMIT 7";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_progress"}, new Callable<List<WorkoutProgress>>() {
      @Override
      @NonNull
      public List<WorkoutProgress> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfSplitCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "splitCompleted");
          final int _cursorIndexOfTotalExercises = CursorUtil.getColumnIndexOrThrow(_cursor, "totalExercises");
          final int _cursorIndexOfCompletedExercises = CursorUtil.getColumnIndexOrThrow(_cursor, "completedExercises");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final List<WorkoutProgress> _result = new ArrayList<WorkoutProgress>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutProgress _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpSplitCompleted;
            _tmpSplitCompleted = _cursor.getString(_cursorIndexOfSplitCompleted);
            final int _tmpTotalExercises;
            _tmpTotalExercises = _cursor.getInt(_cursorIndexOfTotalExercises);
            final int _tmpCompletedExercises;
            _tmpCompletedExercises = _cursor.getInt(_cursorIndexOfCompletedExercises);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            _item = new WorkoutProgress(_tmpId,_tmpDate,_tmpSplitCompleted,_tmpTotalExercises,_tmpCompletedExercises,_tmpDurationMinutes,_tmpCaloriesBurned);
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
