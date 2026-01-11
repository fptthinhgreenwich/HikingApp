package com.example.coursework.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapters.HikeAdapter;
import com.example.coursework.database.HikeDAO;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity for searching hikes with basic and advanced filters
 */
public class SearchActivity extends AppCompatActivity implements HikeAdapter.OnHikeClickListener {

    private TextInputEditText etSearchName, etSearchLocation, etMinLength, etMaxLength, etStartDate, etEndDate;
    private AutoCompleteTextView spinnerDifficulty;
    private MaterialButton btnSearch, btnClear, btnAdvancedSearch;
    private CardView advancedSearchCard;
    private RecyclerView recyclerView;
    private TextView tvEmpty, tvResultsCount;

    private HikeAdapter adapter;
    private HikeDAO hikeDAO;
    private List<Hike> searchResults;
    private boolean advancedSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.search_title);
        }

        // Initialize DAO
        hikeDAO = new HikeDAO(this);

        // Initialize views
        initializeViews();

        // Setup spinner
        setupDifficultySpinner();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup listeners
        setupListeners();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        etSearchName = findViewById(R.id.et_search_name);
        etSearchLocation = findViewById(R.id.et_search_location);
        etMinLength = findViewById(R.id.et_min_length);
        etMaxLength = findViewById(R.id.et_max_length);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);

        spinnerDifficulty = findViewById(R.id.spinner_search_difficulty);

        btnSearch = findViewById(R.id.btn_search);
        btnClear = findViewById(R.id.btn_clear_search);
        btnAdvancedSearch = findViewById(R.id.btn_advanced_search);

        advancedSearchCard = findViewById(R.id.card_advanced_search);
        recyclerView = findViewById(R.id.recycler_view_search);
        tvEmpty = findViewById(R.id.tv_empty_search);
        tvResultsCount = findViewById(R.id.tv_results_count);

        // Initially hide advanced search
        advancedSearchCard.setVisibility(View.GONE);
    }

    /**
     * Sets up the difficulty spinner
     */
    private void setupDifficultySpinner() {
        String[] difficultyLevels = getResources().getStringArray(R.array.difficulty_levels);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficultyLevels);
        spinnerDifficulty.setAdapter(adapter);
    }

    /**
     * Sets up the RecyclerView
     */
    private void setupRecyclerView() {
        searchResults = new ArrayList<>();
        adapter = new HikeAdapter(this, searchResults, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up click listeners
     */
    private void setupListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnClear.setOnClickListener(v -> clearSearch());
        btnAdvancedSearch.setOnClickListener(v -> toggleAdvancedSearch());

        // Date pickers
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etStartDate.setFocusable(false);
        etStartDate.setClickable(true);

        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
        etEndDate.setFocusable(false);
        etEndDate.setClickable(true);
    }

    /**
     * Toggles the visibility of advanced search options
     */
    private void toggleAdvancedSearch() {
        advancedSearchVisible = !advancedSearchVisible;
        advancedSearchCard.setVisibility(advancedSearchVisible ? View.VISIBLE : View.GONE);
        btnAdvancedSearch.setText(advancedSearchVisible ?
                R.string.advanced_search_title : R.string.btn_advanced_search);
    }

    /**
     * Shows date picker dialog
     */
    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();

        // If date exists, use it
        String currentDate = editText.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                calendar.setTime(DateUtils.parseDate(currentDate, Constants.DATE_FORMAT_DATABASE));
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String dateString = DateUtils.formatDate(calendar.getTime(),
                            Constants.DATE_FORMAT_DATABASE);
                    editText.setText(dateString);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    /**
     * Performs search based on entered criteria
     */
    private void performSearch() {
        try {
            String name = etSearchName.getText().toString().trim();
            String location = etSearchLocation.getText().toString().trim();
            String difficulty = spinnerDifficulty.getText().toString().trim();
            String minLengthStr = etMinLength.getText().toString().trim();
            String maxLengthStr = etMaxLength.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();

            // Parse lengths - use null for empty values
            Double minLength = null;
            Double maxLength = null;

            if (!minLengthStr.isEmpty()) {
                try {
                    minLength = Double.parseDouble(minLengthStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid minimum length", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!maxLengthStr.isEmpty()) {
                try {
                    maxLength = Double.parseDouble(maxLengthStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid maximum length", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Perform search
            searchResults = hikeDAO.advancedSearch(name, location, minLength, maxLength,
                    startDate, endDate);

            // Update UI
            adapter.updateList(searchResults);
            updateResultsDisplay();

        } catch (Exception e) {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the results display
     */
    private void updateResultsDisplay() {
        if (searchResults == null || searchResults.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvResultsCount.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            tvResultsCount.setVisibility(View.VISIBLE);
            tvResultsCount.setText(String.format(getString(R.string.results_count),
                    searchResults.size()));
        }
    }

    /**
     * Clears all search fields and results
     */
    private void clearSearch() {
        etSearchName.setText("");
        etSearchLocation.setText("");
        etMinLength.setText("");
        etMaxLength.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        spinnerDifficulty.setText("", false);

        searchResults.clear();
        adapter.updateList(searchResults);
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvResultsCount.setVisibility(View.GONE);
    }

    @Override
    public void onHikeClick(Hike hike) {
        Intent intent = new Intent(this, HikeDetailActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE_ID, hike.getId());
        startActivity(intent);
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
