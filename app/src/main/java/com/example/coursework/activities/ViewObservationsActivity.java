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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapters.ObservationAdapter;
import com.example.coursework.database.ObservationDAO;
import com.example.coursework.models.Observation;
import com.example.coursework.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying all observations for a specific hike
 */
public class ViewObservationsActivity extends AppCompatActivity
        implements ObservationAdapter.OnObservationActionListener {

    private RecyclerView recyclerView;
    private ObservationAdapter adapter;
    private TextView tvEmpty;
    private FloatingActionButton fab;

    private ObservationDAO observationDAO;
    private List<Observation> observationList;
    private long hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_observations);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.view_observations_title);
        }

        // Get hike ID
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_HIKE_ID)) {
            hikeId = intent.getLongExtra(Constants.EXTRA_HIKE_ID, -1);
        } else {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize DAO
        observationDAO = new ObservationDAO(this);

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load observations
        loadObservations();

        // Setup FAB
        setupFab();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_observations);
        tvEmpty = findViewById(R.id.tv_empty_observations);
        fab = findViewById(R.id.fab_add_observation);
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        observationList = new ArrayList<>();
        adapter = new ObservationAdapter(this, observationList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the floating action button
     */
    private void setupFab() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObservationsActivity.this, AddObservationActivity.class);
            intent.putExtra(Constants.EXTRA_HIKE_ID, hikeId);
            startActivity(intent);
        });
    }

    /**
     * Loads all observations for the hike from database
     */
    private void loadObservations() {
        try {
            observationList = observationDAO.getObservationsForHike(hikeId);
            adapter.updateList(observationList);
            updateEmptyState();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the empty state view visibility
     */
    private void updateEmptyState() {
        if (observationList == null || observationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onObservationClick(Observation observation) {
        // Open edit observation activity
        Intent intent = new Intent(this, AddObservationActivity.class);
        intent.putExtra(Constants.EXTRA_OBSERVATION_ID, observation.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Observation observation) {
        showDeleteConfirmation(observation);
    }

    /**
     * Shows confirmation dialog for deleting an observation
     */
    private void showDeleteConfirmation(Observation observation) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_observation_title)
                .setMessage(R.string.dialog_delete_observation_message)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> deleteObservation(observation))
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    /**
     * Deletes an observation from database
     */
    private void deleteObservation(Observation observation) {
        try {
            int deleted = observationDAO.deleteObservation(observation.getId());
            if (deleted > 0) {
                Toast.makeText(this, R.string.success_observation_deleted, Toast.LENGTH_SHORT).show();
                loadObservations();
            } else {
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObservations();
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
