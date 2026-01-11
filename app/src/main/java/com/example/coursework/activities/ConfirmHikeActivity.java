package com.example.coursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.database.HikeDAO;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

/**
 * Activity for confirming hike details before saving to database
 */
public class ConfirmHikeActivity extends AppCompatActivity {

    private TextView tvName, tvLocation, tvDate, tvParking, tvLength;
    private TextView tvDifficulty, tvDescription, tvWeather, tvDuration;
    private MaterialButton btnEdit, btnConfirm;

    private Hike hike;
    private HikeDAO hikeDAO;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hike);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.confirm_title);
        }

        // Initialize DAO
        hikeDAO = new HikeDAO(this);

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
        tvName = findViewById(R.id.tv_name);
        tvLocation = findViewById(R.id.tv_location);
        tvDate = findViewById(R.id.tv_date);
        tvParking = findViewById(R.id.tv_parking);
        tvLength = findViewById(R.id.tv_length);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        tvDescription = findViewById(R.id.tv_description);
        tvWeather = findViewById(R.id.tv_weather);
        tvDuration = findViewById(R.id.tv_duration);

        btnEdit = findViewById(R.id.btn_edit);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    /**
     * Loads hike data from intent and displays it
     */
    private void loadHikeData() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_HIKE)) {
            hike = (Hike) intent.getSerializableExtra(Constants.EXTRA_HIKE);
            if (hike != null) {
                isEditMode = hike.getId() > 0;
                displayHikeDetails();
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
        tvName.setText(hike.getName());
        tvLocation.setText(hike.getLocation());

        // Format date for display
        try {
            String formattedDate = DateUtils.formatDate(
                    DateUtils.parseDate(hike.getDate(), Constants.DATE_FORMAT_DATABASE),
                    Constants.DATE_FORMAT_DISPLAY
            );
            tvDate.setText(formattedDate);
        } catch (Exception e) {
            tvDate.setText(hike.getDate());
        }

        tvParking.setText(hike.getParkingAvailable());
        tvLength.setText(String.format(getString(R.string.km_format), hike.getLength()));
        tvDifficulty.setText(hike.getDifficulty());

        // Set difficulty color
        setDifficultyColor(hike.getDifficulty());

        // Optional fields
        if (hike.getDescription() != null && !hike.getDescription().isEmpty()) {
            tvDescription.setText(hike.getDescription());
        } else {
            tvDescription.setText("-");
        }

        if (hike.getWeatherCondition() != null && !hike.getWeatherCondition().isEmpty()) {
            tvWeather.setText(hike.getWeatherCondition());
        } else {
            tvWeather.setText("-");
        }

        if (hike.getEstimatedDuration() != null && !hike.getEstimatedDuration().isEmpty()) {
            tvDuration.setText(hike.getEstimatedDuration());
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
     * Sets up button click listeners
     */
    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            // Return to edit form
            Intent intent = new Intent(this, AddHikeActivity.class);
            intent.putExtra(Constants.EXTRA_HIKE, hike);
            intent.putExtra(Constants.EXTRA_FROM_CONFIRM, true);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(v -> saveHike());
    }

    /**
     * Saves the hike to the database
     */
    private void saveHike() {
        try {
            long result;
            if (isEditMode) {
                // Update existing hike
                result = hikeDAO.updateHike(hike);
                if (result > 0) {
                    Toast.makeText(this, R.string.success_hike_updated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Insert new hike
                result = hikeDAO.insertHike(hike);
                if (result > 0) {
                    hike.setId(result);
                    Toast.makeText(this, R.string.success_hike_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Return success
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
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
