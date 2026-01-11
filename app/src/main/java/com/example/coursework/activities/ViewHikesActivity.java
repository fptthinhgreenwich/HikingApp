package com.example.coursework.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.example.coursework.adapters.HikeAdapter;
import com.example.coursework.database.HikeDAO;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying all hikes in a RecyclerView list
 */
public class ViewHikesActivity extends AppCompatActivity implements HikeAdapter.OnHikeClickListener {

    private RecyclerView recyclerView;
    private HikeAdapter adapter;
    private TextView tvEmpty;
    private FloatingActionButton fab;

    private HikeDAO hikeDAO;
    private List<Hike> hikeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hikes);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.view_hikes_title);
        }

        // Initialize DAO
        hikeDAO = new HikeDAO(this);

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load hikes
        loadHikes();

        // Setup FAB
        setupFab();
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_hikes);
        tvEmpty = findViewById(R.id.tv_empty_hikes);
        fab = findViewById(R.id.fab_add_hike);
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        hikeList = new ArrayList<>();
        adapter = new HikeAdapter(this, hikeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the floating action button
     */
    private void setupFab() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ViewHikesActivity.this, AddHikeActivity.class);
            startActivityForResult(intent, Constants.REQUEST_ADD_HIKE);
        });
    }

    /**
     * Loads all hikes from database
     */
    private void loadHikes() {
        try {
            hikeList = hikeDAO.getAllHikes();
            adapter.updateList(hikeList);
            updateEmptyState();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the empty state view visibility
     */
    private void updateEmptyState() {
        if (hikeList == null || hikeList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHikeClick(Hike hike) {
        Intent intent = new Intent(this, HikeDetailActivity.class);
        intent.putExtra(Constants.EXTRA_HIKE_ID, hike.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_hikes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete_all) {
            showDeleteAllConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows confirmation dialog for deleting all hikes
     */
    private void showDeleteAllConfirmation() {
        if (hikeList == null || hikeList.isEmpty()) {
            Toast.makeText(this, R.string.empty_hikes, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_all_title)
                .setMessage(R.string.dialog_delete_all_message)
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> deleteAllHikes())
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    /**
     * Deletes all hikes from database
     */
    private void deleteAllHikes() {
        try {
            int deleted = hikeDAO.deleteAllHikes();
            if (deleted > 0) {
                Toast.makeText(this, R.string.success_all_deleted, Toast.LENGTH_SHORT).show();
                loadHikes();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHikes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ADD_HIKE && resultCode == RESULT_OK) {
            loadHikes();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
