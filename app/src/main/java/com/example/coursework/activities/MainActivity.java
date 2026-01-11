package com.example.coursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Main dashboard activity providing navigation to all major app features
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        try {
            databaseHelper = new DatabaseHelper(this);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        // Initialize buttons
        initializeButtons();
    }

    /**
     * Initializes all navigation buttons with click listeners
     */
    private void initializeButtons() {
        MaterialButton btnAddHike = findViewById(R.id.btn_add_hike);
        MaterialButton btnViewHikes = findViewById(R.id.btn_view_hikes);
        MaterialButton btnSearchHikes = findViewById(R.id.btn_search_hikes);

        btnAddHike.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);
            startActivity(intent);
        });

        btnViewHikes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewHikesActivity.class);
            startActivity(intent);
        });

        btnSearchHikes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
