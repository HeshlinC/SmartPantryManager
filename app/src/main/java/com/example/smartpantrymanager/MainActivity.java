package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private TextView textEmpty;
    private BottomNavigationView bottomNav;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        textEmpty = findViewById(R.id.textEmpty);
        bottomNav = findViewById(R.id.bottomNav);

        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tapping an item opens the edit screen, passing its id in the Intent
        adapter = new PantryAdapter(pantryItems, item -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // The + button opens the same screen in "add" mode (no id passed)
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditItemActivity.class)));

        setupBottomNavigation();
    }

    // Bottom navigation: Recipes and Settings screens are added in later commits
    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                return false;
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    // Reads all items from the database and refreshes the list
    private void loadPantryItems() {
        pantryItems.clear();
        pantryItems.addAll(dbHelper.getAllPantryItems());
        adapter.notifyDataSetChanged();
        textEmpty.setVisibility(pantryItems.isEmpty() ? View.VISIBLE : View.GONE);
    }
}