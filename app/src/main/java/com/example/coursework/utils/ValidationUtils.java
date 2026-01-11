package com.example.coursework.utils;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Utility class for input validation
 */
public class ValidationUtils {

    /**
     * Validate if a text field is not empty
     *
     * @param textInputLayout The TextInputLayout to validate
     * @param errorMessage    The error message to display
     * @return true if valid, false otherwise
     */
    public static boolean validateNotEmpty(TextInputLayout textInputLayout, String errorMessage) {
        String text = "";
        if (textInputLayout.getEditText() != null) {
            text = textInputLayout.getEditText().getText().toString().trim();
        }

        if (text.isEmpty()) {
            textInputLayout.setError(errorMessage);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    /**
     * Validate if a number is positive
     *
     * @param textInputLayout The TextInputLayout to validate
     * @param emptyError      Error message for empty field
     * @param invalidError    Error message for invalid value
     * @return true if valid, false otherwise
     */
    public static boolean validatePositiveNumber(TextInputLayout textInputLayout, String emptyError, String invalidError) {
        if (!validateNotEmpty(textInputLayout, emptyError)) {
            return false;
        }

        String text = "";
        if (textInputLayout.getEditText() != null) {
            text = textInputLayout.getEditText().getText().toString().trim();
        }

        try {
            double value = Double.parseDouble(text);
            if (value <= 0) {
                textInputLayout.setError(invalidError);
                return false;
            } else {
                textInputLayout.setError(null);
                return true;
            }
        } catch (NumberFormatException e) {
            textInputLayout.setError(invalidError);
            return false;
        }
    }

    /**
     * Clear error from TextInputLayout
     *
     * @param textInputLayout The TextInputLayout to clear error from
     */
    public static void clearError(TextInputLayout textInputLayout) {
        if (textInputLayout != null) {
            textInputLayout.setError(null);
        }
    }

    /**
     * Get text from TextInputLayout
     *
     * @param textInputLayout The TextInputLayout
     * @return The text content
     */
    public static String getText(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText() != null) {
            return textInputLayout.getEditText().getText().toString().trim();
        }
        return "";
    }

    /**
     * Set text to TextInputLayout
     *
     * @param textInputLayout The TextInputLayout
     * @param text            The text to set
     */
    public static void setText(TextInputLayout textInputLayout, String text) {
        if (textInputLayout.getEditText() != null) {
            textInputLayout.getEditText().setText(text);
        }
    }

    /**
     * Validate if name is valid (not empty)
     *
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Validate if location is valid (not empty)
     *
     * @param location Location to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }

    /**
     * Validate if date is valid (not empty)
     *
     * @param date Date to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String date) {
        return date != null && !date.trim().isEmpty();
    }

    /**
     * Validate if length is valid (positive number)
     *
     * @param lengthStr Length string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLength(String lengthStr) {
        if (lengthStr == null || lengthStr.trim().isEmpty()) {
            return false;
        }
        try {
            double length = Double.parseDouble(lengthStr.trim());
            return length > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate if observation is valid (not empty)
     *
     * @param observation Observation to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidObservation(String observation) {
        return observation != null && !observation.trim().isEmpty();
    }
}
