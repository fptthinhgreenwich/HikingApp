package com.example.coursework.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Hike operations
 * Handles all CRUD operations for hikes table
 */
public class HikeDAO {
    private DatabaseHelper dbHelper;

    public HikeDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new hike into the database
     *
     * @param hike Hike object to insert
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertHike(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_NAME, hike.getName());
            values.put(Constants.COLUMN_LOCATION, hike.getLocation());
            values.put(Constants.COLUMN_DATE, hike.getDate());
            values.put(Constants.COLUMN_PARKING_AVAILABLE, hike.getParkingAvailable());
            values.put(Constants.COLUMN_LENGTH, hike.getLength());
            values.put(Constants.COLUMN_DIFFICULTY, hike.getDifficulty());
            values.put(Constants.COLUMN_DESCRIPTION, hike.getDescription());
            values.put(Constants.COLUMN_WEATHER_CONDITION, hike.getWeatherCondition());
            values.put(Constants.COLUMN_ESTIMATED_DURATION, hike.getEstimatedDuration());

            id = db.insert(Constants.TABLE_HIKES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Get all hikes from the database
     *
     * @return List of all hikes
     */
    public List<Hike> getAllHikes() {
        List<Hike> hikeList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Constants.TABLE_HIKES + " ORDER BY " + Constants.COLUMN_DATE + " DESC";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    hikeList.add(cursorToHike(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hikeList;
    }

    /**
     * Get a hike by ID
     *
     * @param id The hike ID
     * @return Hike object or null if not found
     */
    public Hike getHikeById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Hike hike = null;

        try {
            cursor = db.query(Constants.TABLE_HIKES,
                    null,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                hike = cursorToHike(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hike;
    }

    /**
     * Update an existing hike
     *
     * @param hike Hike object with updated data
     * @return Number of rows affected
     */
    public int updateHike(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_NAME, hike.getName());
            values.put(Constants.COLUMN_LOCATION, hike.getLocation());
            values.put(Constants.COLUMN_DATE, hike.getDate());
            values.put(Constants.COLUMN_PARKING_AVAILABLE, hike.getParkingAvailable());
            values.put(Constants.COLUMN_LENGTH, hike.getLength());
            values.put(Constants.COLUMN_DIFFICULTY, hike.getDifficulty());
            values.put(Constants.COLUMN_DESCRIPTION, hike.getDescription());
            values.put(Constants.COLUMN_WEATHER_CONDITION, hike.getWeatherCondition());
            values.put(Constants.COLUMN_ESTIMATED_DURATION, hike.getEstimatedDuration());

            rowsAffected = db.update(Constants.TABLE_HIKES,
                    values,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(hike.getId())});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /**
     * Delete a hike by ID
     * Also deletes all associated observations due to foreign key constraint
     *
     * @param id The hike ID
     * @return Number of rows affected
     */
    public int deleteHike(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            rowsAffected = db.delete(Constants.TABLE_HIKES,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /**
     * Delete all hikes from the database
     *
     * @return Number of rows deleted
     */
    public int deleteAllHikes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            rowsAffected = db.delete(Constants.TABLE_HIKES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /**
     * Search hikes by name (case-insensitive, partial match)
     *
     * @param name Search query
     * @return List of matching hikes
     */
    public List<Hike> searchHikes(String name) {
        List<Hike> hikeList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Constants.TABLE_HIKES,
                    null,
                    Constants.COLUMN_NAME + " LIKE ?",
                    new String[]{"%" + name + "%"},
                    null, null,
                    Constants.COLUMN_DATE + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    hikeList.add(cursorToHike(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hikeList;
    }

    /**
     * Advanced search with multiple criteria
     *
     * @param name      Name filter (partial match)
     * @param location  Location filter (partial match)
     * @param minLength Minimum length filter
     * @param maxLength Maximum length filter
     * @param startDate Start date filter
     * @param endDate   End date filter
     * @return List of matching hikes
     */
    public List<Hike> advancedSearch(String name, String location, Double minLength,
                                     Double maxLength, String startDate, String endDate) {
        List<Hike> hikeList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            StringBuilder selection = new StringBuilder("1=1");
            List<String> selectionArgs = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                selection.append(" AND ").append(Constants.COLUMN_NAME).append(" LIKE ?");
                selectionArgs.add("%" + name + "%");
            }

            if (location != null && !location.isEmpty()) {
                selection.append(" AND ").append(Constants.COLUMN_LOCATION).append(" LIKE ?");
                selectionArgs.add("%" + location + "%");
            }

            if (minLength != null) {
                selection.append(" AND ").append(Constants.COLUMN_LENGTH).append(" >= ?");
                selectionArgs.add(String.valueOf(minLength));
            }

            if (maxLength != null) {
                selection.append(" AND ").append(Constants.COLUMN_LENGTH).append(" <= ?");
                selectionArgs.add(String.valueOf(maxLength));
            }

            if (startDate != null && !startDate.isEmpty()) {
                selection.append(" AND ").append(Constants.COLUMN_DATE).append(" >= ?");
                selectionArgs.add(startDate);
            }

            if (endDate != null && !endDate.isEmpty()) {
                selection.append(" AND ").append(Constants.COLUMN_DATE).append(" <= ?");
                selectionArgs.add(endDate);
            }

            cursor = db.query(Constants.TABLE_HIKES,
                    null,
                    selection.toString(),
                    selectionArgs.toArray(new String[0]),
                    null, null,
                    Constants.COLUMN_DATE + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    hikeList.add(cursorToHike(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hikeList;
    }

    /**
     * Convert cursor to Hike object
     *
     * @param cursor Database cursor
     * @return Hike object
     */
    private Hike cursorToHike(Cursor cursor) {
        Hike hike = new Hike();
        hike.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_ID)));
        hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_NAME)));
        hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_LOCATION)));
        hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DATE)));
        hike.setParkingAvailable(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_PARKING_AVAILABLE)));
        hike.setLength(cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COLUMN_LENGTH)));
        hike.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DIFFICULTY)));
        hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_DESCRIPTION)));
        hike.setWeatherCondition(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_WEATHER_CONDITION)));
        hike.setEstimatedDuration(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_ESTIMATED_DURATION)));
        hike.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_CREATED_AT)));
        return hike;
    }
}
