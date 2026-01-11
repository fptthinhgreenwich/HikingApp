package com.example.coursework.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;
import com.example.coursework.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

/**
 * Activity for adding or editing a hike with form validation
 */
public class AddHikeActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilLocation, tilDate, tilLength, tilDescription;
    private TextInputLayout tilParking, tilDifficulty, tilWeather, tilDuration;
    private TextInputEditText etName, etLocation, etDate, etLength, etDescription, etDuration;
    private AutoCompleteTextView spinnerParking, spinnerDifficulty, spinnerWeather;
    private MaterialButton btnSave, btnCancel;

    private Hike currentHike;
    private boolean isEditMode = false;
    private boolean fromConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();

        // Setup spinners
        setupSpinners();

        // Check if editing existing hike
        checkEditMode();

        // Setup listeners
        setupListeners();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        tilName = findViewById(R.id.til_name);
        tilLocation = findViewById(R.id.til_location);
        tilDate = findViewById(R.id.til_date);
        tilLength = findViewById(R.id.til_length);
        tilDescription = findViewById(R.id.til_description);
        tilParking = findViewById(R.id.til_parking);
        tilDifficulty = findViewById(R.id.til_difficulty);
        tilWeather = findViewById(R.id.til_weather);
        tilDuration = findViewById(R.id.til_duration);

        etName = findViewById(R.id.et_name);
        etLocation = findViewById(R.id.et_location);
        etDate = findViewById(R.id.et_date);
        etLength = findViewById(R.id.et_length);
        etDescription = findViewById(R.id.et_description);
        etDuration = findViewById(R.id.et_duration);

        spinnerParking = findViewById(R.id.spinner_parking);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerWeather = findViewById(R.id.spinner_weather);

        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    /**
     * Sets up dropdown spinners with data from resources
     */
    private void setupSpinners() {
        // Parking availability spinner
        String[] parkingOptions = getResources().getStringArray(R.array.parking_options);
        ArrayAdapter<String> parkingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, parkingOptions);
        spinnerParking.setAdapter(parkingAdapter);

        // Difficulty level spinner
        String[] difficultyLevels = getResources().getStringArray(R.array.difficulty_levels);
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficultyLevels);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        // Weather condition spinner
        String[] weatherConditions = getResources().getStringArray(R.array.weather_conditions);
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, weatherConditions);
        spinnerWeather.setAdapter(weatherAdapter);
    }

    /**
     * Checks if activity was opened in edit mode and loads hike data
     */
    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_HIKE)) {
            currentHike = (Hike) intent.getSerializableExtra(Constants.EXTRA_HIKE);
            if (currentHike != null) {
                isEditMode = true;
                fromConfirm = intent.getBooleanExtra(Constants.EXTRA_FROM_CONFIRM, false);
                setTitle(R.string.edit_hike_title);
                populateFields();
            }
        } else {
            setTitle(R.string.add_hike_title);
            currentHike = new Hike();
        }
    }

    /**
     * Populates form fields with existing hike data
     */
    private void populateFields() {
        etName.setText(currentHike.getName());
        etLocation.setText(currentHike.getLocation());
        etDate.setText(currentHike.getDate());
        etLength.setText(String.valueOf(currentHike.getLength()));
        etDescription.setText(currentHike.getDescription());
        etDuration.setText(currentHike.getEstimatedDuration());

        spinnerParking.setText(currentHike.getParkingAvailable(), false);
        spinnerDifficulty.setText(currentHike.getDifficulty(), false);
        spinnerWeather.setText(currentHike.getWeatherCondition(), false);
    }

    /**
     * Sets up click listeners for all interactive components
     */
    private void setupListeners() {
        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());
        etDate.setFocusable(false);
        etDate.setClickable(true);

        // Save button
        btnSave.setOnClickListener(v -> validateAndSave());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Shows date picker dialog for selecting hike date
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // If editing and date exists, use it
        if (currentHike != null && currentHike.getDate() != null && !currentHike.getDate().isEmpty()) {
            try {
                calendar.setTime(DateUtils.parseDate(currentHike.getDate(), Constants.DATE_FORMAT_DATABASE));
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String dateString = DateUtils.formatDate(calendar.getTime(), Constants.DATE_FORMAT_DATABASE);
                    etDate.setText(dateString);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CONFIRM_HIKE && resultCode == RESULT_OK) {
            // Hike was confirmed and saved
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * Validates form input and proceeds to confirmation
     */
    private void validateAndSave() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String lengthStr = etLength.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String parking = spinnerParking.getText().toString().trim();
        String difficulty = spinnerDifficulty.getText().toString().trim();
        String weather = spinnerWeather.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();

        boolean isValid = true;

        // Validate name
        if (!ValidationUtils.isValidName(name)) {
            tilName.setError(getString(R.string.error_name_required));
            isValid = false;
        }

        // Validate location
        if (!ValidationUtils.isValidLocation(location)) {
            tilLocation.setError(getString(R.string.error_location_required));
            isValid = false;
        }

        // Validate date
        if (!ValidationUtils.isValidDate(date)) {
            tilDate.setError(getString(R.string.error_date_required));
            isValid = false;
        }

        // Validate parking
        if (parking.isEmpty()) {
            tilParking.setError(getString(R.string.error_parking_required));
            isValid = false;
        }

        // Validate length
        double length = 0;
        if (!ValidationUtils.isValidLength(lengthStr)) {
            tilLength.setError(getString(R.string.error_length_required));
            isValid = false;
        } else {
            try {
                length = Double.parseDouble(lengthStr);
                if (length <= 0) {
                    tilLength.setError(getString(R.string.error_length_invalid));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilLength.setError(getString(R.string.error_length_invalid));
                isValid = false;
            }
        }

        // Validate difficulty
        if (difficulty.isEmpty()) {
            tilDifficulty.setError(getString(R.string.error_difficulty_required));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Create or update hike object
        currentHike.setName(name);
        currentHike.setLocation(location);
        currentHike.setDate(date);
        currentHike.setParkingAvailable(parking);
        currentHike.setLength(length);
        currentHike.setDifficulty(difficulty);
        currentHike.setDescription(description);
        currentHike.setWeatherCondition(weather);
        currentHike.setEstimatedDuration(duration);

        // Go to confirmation screen
        Intent intent = new Intent(this, ConfirmHikeActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE, currentHike);
        startActivityForResult(intent, Constants.REQUEST_CONFIRM_HIKE);
    }

    /**
     * Clears all error messages from input fields
     */
    private void clearErrors() {
        tilName.setError(null);
        tilLocation.setError(null);
        tilDate.setError(null);
        tilLength.setError(null);
        tilParking.setError(null);
        tilDifficulty.setError(null);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
