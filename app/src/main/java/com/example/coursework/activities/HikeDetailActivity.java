package com.example.coursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.database.HikeDAO;
import com.example.coursework.database.ObservationDAO;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

/**
 * Activity for viewing detailed information about a single hike
 */
public class HikeDetailActivity extends AppCompatActivity {

    private TextView tvName, tvLocation, tvDate, tvParking, tvLength;
    private TextView tvDifficulty, tvDescription, tvWeather, tvDuration, tvObservationCount;
    private MaterialButton btnEdit, btnDelete, btnAddObservation, btnViewObservations;

    private HikeDAO hikeDAO;
    private ObservationDAO observationDAO;
    private Hike currentHike;
    private long hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.hike_detail_title);
        }

        // Initialize DAOs
        hikeDAO = new HikeDAO(this);
        observationDAO = new ObservationDAO(this);

        // Initialize views
        initializeViews();

        // Load hike data
        loadHikeData();

        // Setup listeners
        setupListeners();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        tvName = findViewById(R.id.tv_detail_name);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvDate = findViewById(R.id.tv_detail_date);
        tvParking = findViewById(R.id.tv_detail_parking);
        tvLength = findViewById(R.id.tv_detail_length);
        tvDifficulty = findViewById(R.id.tv_detail_difficulty);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvWeather = findViewById(R.id.tv_detail_weather);
        tvDuration = findViewById(R.id.tv_detail_duration);
        tvObservationCount = findViewById(R.id.tv_observation_count);

        btnEdit = findViewById(R.id.btn_edit_hike);
        btnDelete = findViewById(R.id.btn_delete_hike);
        btnAddObservation = findViewById(R.id.btn_add_observation);
        btnViewObservations = findViewById(R.id.btn_view_observations);
    }

    /**
     * Loads hike data from database
     */
    private void loadHikeData() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_HIKE_ID)) {
            hikeId = intent.getLongExtra(Constants.EXTRA_HIKE_ID, -1);
            if (hikeId != -1) {
                try {
                    currentHike = hikeDAO.getHikeById(hikeId);
                    if (currentHike != null) {
                        displayHikeDetails();
                        updateObservationCount();
                    } else {
                        Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Displays hike details in the UI
     */
    private void displayHikeDetails() {
        tvName.setText(currentHike.getName());
        tvLocation.setText(currentHike.getLocation());

        // Format date for display
        try {
            String formattedDate = DateUtils.formatDate(
                    DateUtils.parseDate(currentHike.getDate(), Constants.DATE_FORMAT_DATABASE),
                    Constants.DATE_FORMAT_DISPLAY
            );
            tvDate.setText(formattedDate);
        } catch (Exception e) {
            tvDate.setText(currentHike.getDate());
        }

        tvParking.setText(currentHike.getParkingAvailable());
        tvLength.setText(String.format(getString(R.string.km_format), currentHike.getLength()));
        tvDifficulty.setText(currentHike.getDifficulty());

        // Set difficulty color
        setDifficultyColor(currentHike.getDifficulty());

        // Optional fields
        if (currentHike.getDescription() != null && !currentHike.getDescription().isEmpty()) {
            tvDescription.setText(currentHike.getDescription());
        } else {
            tvDescription.setText("-");
        }

        if (currentHike.getWeatherCondition() != null && !currentHike.getWeatherCondition().isEmpty()) {
            tvWeather.setText(currentHike.getWeatherCondition());
        } else {
            tvWeather.setText("-");
        }

        if (currentHike.getEstimatedDuration() != null && !currentHike.getEstimatedDuration().isEmpty()) {
            tvDuration.setText(currentHike.getEstimatedDuration());
        } else {
            tvDuration.setText("-");
        }

    }

    /**
     * Sets the difficulty badge color based on difficulty level
     */
    private void setDifficultyColor(String difficulty) {
        int colorResId;
        switch (difficulty.toLowerCase()) {
            case "easy":
                colorResId = R.color.difficulty_easy;
                break;
            case "moderate":
                colorResId = R.color.difficulty_moderate;
                break;
            case "hard":
                colorResId = R.color.difficulty_hard;
                break;
            case "expert":
                colorResId = R.color.difficulty_expert;
                break;
            default:
                colorResId = R.color.text_secondary;
        }
        tvDifficulty.setTextColor(getResources().getColor(colorResId, getTheme()));
    }

    /**
     * Updates the observation count display
     */
    private void updateObservationCount() {
        try {
            int count = observationDAO.getObservationCountForHike(hikeId);
            tvObservationCount.setText(String.format(getString(R.string.observations_count), count));
        } catch (Exception e) {
            tvObservationCount.setText(String.format(getString(R.string.observations_count), 0));
        }
    }

    /**
     * Sets up button click listeners
     */
    private void setupListeners() {
        btnEdit.setOnClickListener(v -> editHike());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnAddObservation.setOnClickListener(v -> addObservation());
        btnViewObservations.setOnClickListener(v -> viewObservations());
    }

    /**
     * Opens the edit hike activity
     */
    private void editHike() {
        Intent intent = new Intent(this, AddHikeActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE, currentHike);
        startActivity(intent);
    }

    /**
     * Shows confirmation dialog for deleting the hike
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_hike_title)
                .setMessage(R.string.dialog_delete_hike_message)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> deleteHike())
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    /**
     * Deletes the current hike from database
     */
    private void deleteHike() {
        try {
            int deleted = hikeDAO.deleteHike(hikeId);
            if (deleted > 0) {
                Toast.makeText(this, R.string.success_hike_deleted, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the add observation activity
     */
    private void addObservation() {
        Intent intent = new Intent(this, AddObservationActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE_ID, hikeId);
        startActivity(intent);
    }

    /**
     * Opens the view observations activity
     */
    private void viewObservations() {
        Intent intent = new Intent(this, ViewObservationsActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE_ID, hikeId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning from edit
        if (currentHike != null) {
            loadHikeData();
        }
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
