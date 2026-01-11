package com.example.coursework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.coursework.utils.Constants;

/**
 * SQLite database helper class
 * Manages database creation and version management
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Create hikes table SQL
    private static final String CREATE_TABLE_HIKES = "CREATE TABLE " + Constants.TABLE_HIKES + " ("
            + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Constants.COLUMN_NAME + " TEXT NOT NULL, "
            + Constants.COLUMN_LOCATION + " TEXT NOT NULL, "
            + Constants.COLUMN_DATE + " TEXT NOT NULL, "
            + Constants.COLUMN_PARKING_AVAILABLE + " TEXT NOT NULL, "
            + Constants.COLUMN_LENGTH + " REAL NOT NULL, "
            + Constants.COLUMN_DIFFICULTY + " TEXT NOT NULL, "
            + Constants.COLUMN_DESCRIPTION + " TEXT, "
            + Constants.COLUMN_WEATHER_CONDITION + " TEXT, "
            + Constants.COLUMN_ESTIMATED_DURATION + " TEXT, "
            + Constants.COLUMN_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // Create observations table SQL
    private static final String CREATE_TABLE_OBSERVATIONS = "CREATE TABLE " + Constants.TABLE_OBSERVATIONS + " ("
            + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Constants.COLUMN_HIKE_ID + " INTEGER NOT NULL, "
            + Constants.COLUMN_OBSERVATION + " TEXT NOT NULL, "
            + Constants.COLUMN_TIME + " TEXT NOT NULL, "
            + Constants.COLUMN_COMMENTS + " TEXT, "
            + "FOREIGN KEY(" + Constants.COLUMN_HIKE_ID + ") REFERENCES "
            + Constants.TABLE_HIKES + "(" + Constants.COLUMN_ID + ") ON DELETE CASCADE"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_HIKES);
        db.execSQL(CREATE_TABLE_OBSERVATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_OBSERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_HIKES);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }
}
