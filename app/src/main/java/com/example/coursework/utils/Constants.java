package com.example.coursework.utils;

/**
 * Application-wide constants
 */
public class Constants {
    // Database constants
    public static final String DATABASE_NAME = "mhike.db";
    public static final int DATABASE_VERSION = 3; // Incremented for removing photo_path column

    // Table names
    public static final String TABLE_HIKES = "hikes";
    public static final String TABLE_OBSERVATIONS = "observations";

    // Hikes table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PARKING_AVAILABLE = "parking_available";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_WEATHER_CONDITION = "weather_condition";
    public static final String COLUMN_ESTIMATED_DURATION = "estimated_duration";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Observations table columns
    public static final String COLUMN_HIKE_ID = "hike_id";
    public static final String COLUMN_OBSERVATION = "observation";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_COMMENTS = "comments";

    // Intent extras
    public static final String EXTRA_HIKE_ID = "hike_id";
    public static final String EXTRA_HIKE = "hike";
    public static final String EXTRA_OBSERVATION_ID = "observation_id";
    public static final String EXTRA_FROM_CONFIRM = "from_confirm";

    // Date formats
    public static final String DATE_FORMAT_DATABASE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DISPLAY = "MMMM dd, yyyy";
    public static final String DATETIME_FORMAT_DATABASE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy hh:mm a";

    // Request codes
    public static final int REQUEST_ADD_HIKE = 100;
    public static final int REQUEST_EDIT_HIKE = 101;
    public static final int REQUEST_CONFIRM_HIKE = 102;
}
