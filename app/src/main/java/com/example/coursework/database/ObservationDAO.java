package com.example.coursework.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coursework.models.Observation;
import com.example.coursework.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Observation operations
 * Handles all CRUD operations for observations table
 */
public class ObservationDAO {
    private DatabaseHelper dbHelper;

    public ObservationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new observation into the database
     *
     * @param observation Observation object to insert
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertObservation(Observation observation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_HIKE_ID, observation.getHikeId());
            values.put(Constants.COLUMN_OBSERVATION, observation.getObservation());
            values.put(Constants.COLUMN_TIME, observation.getTime());
            values.put(Constants.COLUMN_COMMENTS, observation.getComments());

            id = db.insert(Constants.TABLE_OBSERVATIONS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Get all observations for a specific hike
     *
     * @param hikeId The hike ID
     * @return List of observations for the hike
     */
    public List<Observation> getObservationsForHike(long hikeId) {
        List<Observation> observationList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Constants.TABLE_OBSERVATIONS,
                    null,
                    Constants.COLUMN_HIKE_ID + "=?",
                    new String[]{String.valueOf(hikeId)},
                    null, null,
                    Constants.COLUMN_TIME + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    observationList.add(cursorToObservation(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return observationList;
    }

    /**
     * Get an observation by ID
     *
     * @param id The observation ID
     * @return Observation object or null if not found
     */
    public Observation getObservationById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Observation observation = null;

        try {
            cursor = db.query(Constants.TABLE_OBSERVATIONS,
                    null,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                observation = cursorToObservation(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return observation;
    }

    /**
     * Update an existing observation
     *
     * @param observation Observation object with updated data
     * @return Number of rows affected
     */
    public int updateObservation(Observation observation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_OBSERVATION, observation.getObservation());
            values.put(Constants.COLUMN_TIME, observation.getTime());
            values.put(Constants.COLUMN_COMMENTS, observation.getComments());

            rowsAffected = db.update(Constants.TABLE_OBSERVATIONS,
                    values,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(observation.getId())});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /**
     * Delete an observation by ID
     *
     * @param id The observation ID
     * @return Number of rows affected
     */
    public int deleteObservation(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        try {
            rowsAffected = db.delete(Constants.TABLE_OBSERVATIONS,
                    Constants.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /**
     * Get total count of observations for a hike
     *
     * @param hikeId The hike ID
     * @return Count of observations
     */
    public int getObservationCount(long hikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + Constants.TABLE_OBSERVATIONS +
                    " WHERE " + Constants.COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(hikeId)});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    /**
     * Alias for getObservationCount
     */
    public int getObservationCountForHike(long hikeId) {
        return getObservationCount(hikeId);
    }

    /**
     * Convert cursor to Observation object
     *
     * @param cursor Database cursor
     * @return Observation object
     */
    private Observation cursorToObservation(Cursor cursor) {
        Observation observation = new Observation();
        observation.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_ID)));
        observation.setHikeId(cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COLUMN_HIKE_ID)));
        observation.setObservation(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_OBSERVATION)));
        observation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_TIME)));
        observation.setComments(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_COMMENTS)));
        return observation;
    }
}
