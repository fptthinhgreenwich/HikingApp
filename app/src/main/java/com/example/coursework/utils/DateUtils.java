package com.example.coursework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date formatting and parsing operations
 */
public class DateUtils {

    /**
     * Format a date string from database format to display format
     *
     * @param dateString Date in format yyyy-MM-dd
     * @return Date in format MMMM dd, yyyy or original string if parsing fails
     */
    public static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    /**
     * Format a datetime string from database format to display format
     *
     * @param dateTimeString DateTime in format yyyy-MM-dd HH:mm:ss
     * @return DateTime in format MMM dd, yyyy hh:mm a or original string if parsing fails
     */
    public static String formatDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_DATABASE, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_DISPLAY, Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);
            return date != null ? outputFormat.format(date) : dateTimeString;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeString;
        }
    }

    /**
     * Get current date in database format
     *
     * @return Current date as yyyy-MM-dd
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Get current datetime in database format
     *
     * @return Current datetime as yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT_DATABASE, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Convert Date object to database format string
     *
     * @param date Date object
     * @return Date as yyyy-MM-dd
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Parse database format date string to Date object
     *
     * @param dateString Date as yyyy-MM-dd
     * @return Date object or null if parsing fails
     */
    public static Date stringToDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse date string with custom format to Date object
     *
     * @param dateString Date string
     * @param format Date format pattern
     * @return Date object or null if parsing fails
     */
    public static Date parseDate(String dateString, String format) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Format Date object to string with custom format
     *
     * @param date Date object
     * @param format Date format pattern
     * @return Formatted date string
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }
}
