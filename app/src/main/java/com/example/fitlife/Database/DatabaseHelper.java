package com.example.fitlife.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fitlife.Model.Exercise;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.Model.WorkoutLocation;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "FitLifeDB";
    private static final int DATABASE_VERSION = 1;
    //Database Table Name
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_LOCATIONS = "workout_locations";
    //Column Id
    private static final String COL_ID = "id";

    //WorkOut Database  Table Column
    private static final String COL_NAME = "name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_EQUIPMENT = "equipment";
    private static final String COL_DURATION = "duration";
    private static final String COL_IMAGE_PATH = "image_path";
    private static final String COL_IS_COMPLETED = "is_completed";
    private static final String COL_CREATED_AT = "created_at";

//    EXERCISES Database Table Column
    private static final String COL_WORKOUT_ID = "workout_id";
    private static final String COL_SETS = "sets";
    private static final String COL_REPS = "reps";
    private static final String COL_INSTRUCTIONS = "instructions";

//Location Database Table Column
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_ADDRESS = "address";

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    public DatabaseHelper(Context context, boolean useSingleton) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createWorkoutsTable(db);
        createExercisesTable(db);
        createLocationsTable(db);
        Log.d(TAG, "Database created successfully");
    }
    private void createWorkoutsTable(SQLiteDatabase db) {
        String CREATE_WORKOUTS_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_EQUIPMENT + " TEXT, "
                + COL_DURATION + " INTEGER, "
                + COL_IMAGE_PATH + " TEXT, "
                + COL_IS_COMPLETED + " INTEGER DEFAULT 0, "
                + COL_CREATED_AT + " INTEGER"
                + ")";

        db.execSQL(CREATE_WORKOUTS_TABLE);
        Log.d(TAG, "Workouts table created");
    }
    private void createExercisesTable(SQLiteDatabase db) {
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_WORKOUT_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_SETS + " INTEGER, "
                + COL_REPS + " INTEGER, "
                + COL_INSTRUCTIONS + " TEXT, "
                + COL_IS_COMPLETED + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COL_WORKOUT_ID + ") REFERENCES "
                + TABLE_WORKOUTS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ")";

        db.execSQL(CREATE_EXERCISES_TABLE);
        Log.d(TAG, "Exercises table created");
    }
    private void createLocationsTable(SQLiteDatabase db) {
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_WORKOUT_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_LATITUDE + " REAL, "
                + COL_LONGITUDE + " REAL, "
                + COL_ADDRESS + " TEXT, "
                + "FOREIGN KEY(" + COL_WORKOUT_ID + ") REFERENCES "
                + TABLE_WORKOUTS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ")";

        db.execSQL(CREATE_LOCATIONS_TABLE);
        Log.d(TAG, "Locations table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);

        // Create tables again
        onCreate(db);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }
    public long insertWorkout(Workout workout) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_NAME, workout.getName());
            values.put(COL_DESCRIPTION, workout.getDescription());
            values.put(COL_EQUIPMENT, workout.getEquipment());
            values.put(COL_DURATION, workout.getDuration());
            values.put(COL_IMAGE_PATH, workout.getImagePath());
            values.put(COL_IS_COMPLETED, workout.isCompleted() ? 1 : 0);
            values.put(COL_CREATED_AT, workout.getCreatedAt());

            id = db.insert(TABLE_WORKOUTS, null, values);

            if (id != -1) {
                Log.d(TAG, "Workout inserted successfully with ID: " + id);
            } else {
                Log.e(TAG, "Failed to insert workout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting workout: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return id;
    }
    public Workout getWorkout(long id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Workout workout = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_WORKOUTS,
                    null,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                workout = cursorToWorkout(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting workout: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return workout;
    }
    public List<Workout> getAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_WORKOUTS
                    + " ORDER BY " + COL_CREATED_AT + " DESC";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    workouts.add(cursorToWorkout(cursor));
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Retrieved " + workouts.size() + " workouts");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all workouts: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return workouts;
    }
    public List<Workout> getCompletedWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_WORKOUTS
                    + " WHERE " + COL_IS_COMPLETED + " = 1"
                    + " ORDER BY " + COL_CREATED_AT + " DESC";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    workouts.add(cursorToWorkout(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting completed workouts: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return workouts;
    }
    public int updateWorkout(Workout workout) {
        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_NAME, workout.getName());
            values.put(COL_DESCRIPTION, workout.getDescription());
            values.put(COL_EQUIPMENT, workout.getEquipment());
            values.put(COL_DURATION, workout.getDuration());
            values.put(COL_IMAGE_PATH, workout.getImagePath());
            values.put(COL_IS_COMPLETED, workout.isCompleted() ? 1 : 0);

            result = db.update(
                    TABLE_WORKOUTS,
                    values,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(workout.getId())}
            );

            Log.d(TAG, "Workout updated: " + result + " rows affected");
        } catch (Exception e) {
            Log.e(TAG, "Error updating workout: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return result;
    }
    public void deleteWorkout(long id) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            int result = db.delete(
                    TABLE_WORKOUTS,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            Log.d(TAG, "Workout deleted: " + result + " rows affected");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting workout: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }
    public void markWorkoutCompleted(long id, boolean completed) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_IS_COMPLETED, completed ? 1 : 0);

            int result = db.update(
                    TABLE_WORKOUTS,
                    values,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            Log.d(TAG, "Workout completion updated: " + result + " rows affected");
        } catch (Exception e) {
            Log.e(TAG, "Error marking workout completed: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }
    public int getWorkoutCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_WORKOUTS;
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting workout count: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }
    public int getCompletedWorkoutCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_WORKOUTS
                    + " WHERE " + COL_IS_COMPLETED + " = 1";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting completed count: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }
    public long insertExercise(Exercise exercise) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_WORKOUT_ID, exercise.getWorkoutId());
            values.put(COL_NAME, exercise.getName());
            values.put(COL_SETS, exercise.getSets());
            values.put(COL_REPS, exercise.getReps());
            values.put(COL_INSTRUCTIONS, exercise.getInstructions());
            values.put(COL_IS_COMPLETED, exercise.isCompleted() ? 1 : 0);

            id = db.insert(TABLE_EXERCISES, null, values);
            Log.d(TAG, "Exercise inserted with ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting exercise: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return id;
    }
    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_NAME, exercise.getName());
            values.put(COL_SETS, exercise.getSets());
            values.put(COL_REPS, exercise.getReps());
            values.put(COL_INSTRUCTIONS, exercise.getInstructions());
            values.put(COL_IS_COMPLETED, exercise.isCompleted() ? 1 : 0);

            result = db.update(
                    TABLE_EXERCISES,
                    values,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(exercise.getId())}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating exercise: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return result;
    }
    public void deleteExercise(long id) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_EXERCISES, COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting exercise: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }
    public long insertLocation(WorkoutLocation location) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_WORKOUT_ID, location.getWorkoutId());
            values.put(COL_NAME, location.getName());
            values.put(COL_LATITUDE, location.getLatitude());
            values.put(COL_LONGITUDE, location.getLongitude());
            values.put(COL_ADDRESS, location.getAddress());

            id = db.insert(TABLE_LOCATIONS, null, values);
            Log.d(TAG, "Location inserted with ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting location: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return id;
    }
    public WorkoutLocation getLocationForWorkout(long workoutId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        WorkoutLocation location = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_LOCATIONS
                    + " WHERE " + COL_WORKOUT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(workoutId)});

            if (cursor.moveToFirst()) {
                location = cursorToLocation(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting location: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return location;
    }
    public int updateLocation(WorkoutLocation location) {
        SQLiteDatabase db = null;
        int result = 0;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_NAME, location.getName());
            values.put(COL_LATITUDE, location.getLatitude());
            values.put(COL_LONGITUDE, location.getLongitude());
            values.put(COL_ADDRESS, location.getAddress());

            result = db.update(
                    TABLE_LOCATIONS,
                    values,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(location.getId())}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating location: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return result;
    }
    public void deleteLocation(long id) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_LOCATIONS, COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting location: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }
    private Workout cursorToWorkout(Cursor cursor) {
        Workout workout = new Workout();
        workout.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        workout.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        workout.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
        workout.setEquipment(cursor.getString(cursor.getColumnIndexOrThrow(COL_EQUIPMENT)));
        workout.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DURATION)));
        workout.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_PATH)));
        workout.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_COMPLETED)) == 1);
        workout.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
        return workout;
    }
    private Exercise cursorToExercise(Cursor cursor) {
        Exercise exercise = new Exercise();
        exercise.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        exercise.setWorkoutId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_WORKOUT_ID)));
        exercise.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        exercise.setSets(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETS)));
        exercise.setReps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPS)));
        exercise.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
        exercise.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_COMPLETED)) == 1);
        return exercise;
    }

    /**
     * Convert Cursor to WorkoutLocation object
     */
    private WorkoutLocation cursorToLocation(Cursor cursor) {
        WorkoutLocation location = new WorkoutLocation();
        location.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        location.setWorkoutId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_WORKOUT_ID)));
        location.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)));
        location.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE)));
        location.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS)));
        return location;
    }

    /**
     * Delete all data (for testing purposes)
     */
    public void deleteAllData() {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_LOCATIONS, null, null);
            db.delete(TABLE_EXERCISES, null, null);
            db.delete(TABLE_WORKOUTS, null, null);
            Log.d(TAG, "All data deleted successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all data: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }

}
