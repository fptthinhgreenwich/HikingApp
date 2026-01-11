package com.example.coursework.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.database.HikeDAO;
import com.example.coursework.database.ObservationDAO;
import com.example.coursework.models.Hike;
import com.example.coursework.models.Observation;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;
import com.example.coursework.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

/**
 * Activity for adding or editing an observation for a hike
 */
public class AddObservationActivity extends AppCompatActivity {

    private TextView tvHikeName;
    private TextInputLayout tilObservation, tilTime, tilComments;
    private TextInputEditText etObservation, etTime, etComments;
    private MaterialButton btnSave, btnCancel;

    private ObservationDAO observationDAO;
    private HikeDAO hikeDAO;
    private Observation currentObservation;
    private long hikeId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize DAOs
        observationDAO = new ObservationDAO(this);
        hikeDAO = new HikeDAO(this);

        // Initialize views
        initializeViews();

        // Check mode and load data
        checkEditMode();

        // Setup listeners
        setupListeners();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        tvHikeName = findViewById(R.id.tv_hike_name);
        tilObservation = findViewById(R.id.til_observation);
        tilTime = findViewById(R.id.til_time);
        tilComments = findViewById(R.id.til_comments);

        etObservation = findViewById(R.id.et_observation);
        etTime = findViewById(R.id.et_time);
        etComments = findViewById(R.id.et_comments);

        btnSave = findViewById(R.id.btn_save_observation);
        btnCancel = findViewById(R.id.btn_cancel_observation);
    }

    /**
     * Checks if editing existing observation or adding new one
     */
    private void checkEditMode() {
        Intent intent = getIntent();

        // Check if editing
        if (intent.hasExtra(Constants.EXTRA_OBSERVATION_ID)) {
            isEditMode = true;
            setTitle(R.string.edit_observation_title);
            long observationId = intent.getLongExtra(Constants.EXTRA_OBSERVATION_ID, -1);
            loadObservation(observationId);
        } else if (intent.hasExtra(Constants.EXTRA_HIKE_ID)) {
            // Adding new observation
            setTitle(R.string.add_observation_title);
            hikeId = intent.getLongExtra(Constants.EXTRA_HIKE_ID, -1);
            currentObservation = new Observation();
            currentObservation.setHikeId(hikeId);
            loadHikeName(hikeId);

            // Set current date/time as default
            Calendar calendar = Calendar.getInstance();
            String currentDateTime = DateUtils.formatDate(calendar.getTime(), Constants.DATETIME_FORMAT_DATABASE);
            etTime.setText(currentDateTime);
        } else {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Loads observation data for editing
     */
    private void loadObservation(long observationId) {
        try {
            currentObservation = observationDAO.getObservationById(observationId);
            if (currentObservation != null) {
                hikeId = currentObservation.getHikeId();
                loadHikeName(hikeId);
                populateFields();
            } else {
                Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Loads and displays the hike name
     */
    private void loadHikeName(long hikeId) {
        try {
            Hike hike = hikeDAO.getHikeById(hikeId);
            if (hike != null) {
                tvHikeName.setText(hike.getName());
            }
        } catch (Exception e) {
            tvHikeName.setText("");
        }
    }

    /**
     * Populates form fields with existing observation data
     */
    private void populateFields() {
        etObservation.setText(currentObservation.getObservation());
        etTime.setText(currentObservation.getTime());
        etComments.setText(currentObservation.getComments());
    }

    /**
     * Sets up click listeners
     */
    private void setupListeners() {
        // Time picker
        etTime.setOnClickListener(v -> showDateTimePicker());
        etTime.setFocusable(false);
        etTime.setClickable(true);

        // Save button
        btnSave.setOnClickListener(v -> validateAndSave());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Shows date and time picker dialogs
     */
    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // If editing and time exists, use it
        if (currentObservation != null && currentObservation.getTime() != null
                && !currentObservation.getTime().isEmpty()) {
            try {
                calendar.setTime(DateUtils.parseDate(currentObservation.getTime(),
                        Constants.DATETIME_FORMAT_DATABASE));
            } catch (Exception e) {
                // Use current time if parsing fails
            }
        }

        // Show date picker first
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Then show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                String dateTimeString = DateUtils.formatDate(calendar.getTime(),
                                        Constants.DATETIME_FORMAT_DATABASE);
                                etTime.setText(dateTimeString);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    /**
     * Validates form input and saves observation
     */
    private void validateAndSave() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String observation = etObservation.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        boolean isValid = true;

        // Validate observation
        if (!ValidationUtils.isValidObservation(observation)) {
            tilObservation.setError(getString(R.string.error_observation_required));
            isValid = false;
        }

        // Time is auto-set, but validate anyway
        if (time.isEmpty()) {
            tilTime.setError(getString(R.string.error_observation_required));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Update observation object
        currentObservation.setObservation(observation);
        currentObservation.setTime(time);
        currentObservation.setComments(comments);

        // Save to database
        saveObservation();
    }

    /**
     * Saves the observation to database
     */
    private void saveObservation() {
        try {
            long result;
            if (isEditMode) {
                result = observationDAO.updateObservation(currentObservation);
                if (result > 0) {
                    Toast.makeText(this, R.string.success_observation_updated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                result = observationDAO.insertObservation(currentObservation);
                if (result > 0) {
                    Toast.makeText(this, R.string.success_observation_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Clears all error messages
     */
    private void clearErrors() {
        tilObservation.setError(null);
        tilTime.setError(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
