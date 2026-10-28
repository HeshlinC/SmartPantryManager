package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    // Names used to store and read the setting (also used by PantryAdapter)
    public static final String PREFS_NAME = "pantry_settings";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + pad, bars.top + pad, bars.right + pad, bars.bottom + pad);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        // SharedPreferences keeps simple settings on the device between app launches
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiryAlerts);
        switchExpiry.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        Button buttonClear = findViewById(R.id.buttonClearPantry);
        buttonClear.setOnClickListener(v -> confirmClearPantry());

        Button buttonBack = findViewById(R.id.buttonSettingsBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    // Asks for confirmation, then deletes every pantry item
    private void confirmClearPantry() {
        int count = dbHelper.getAllPantryItems().size();
        if (count == 0) {
            Toast.makeText(this, "Your pantry is already empty", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Clear entire pantry")
                .setMessage("This will delete all " + count + " ingredients. This cannot be undone.")
                .setPositiveButton("Delete all", (dialog, which) -> {
                    dbHelper.clearPantry();
                    Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}