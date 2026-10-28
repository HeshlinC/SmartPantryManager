package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // Items expiring within this many days are highlighted
    private static final int EXPIRING_SOON_DAYS = 3;

    // Lets the Activity decide what happens when an item is tapped
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(item.getQuantity() + " " + item.getUnit());

        // Read the Settings toggle
        SharedPreferences prefs = holder.itemView.getContext()
                .getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        boolean alertsOn = prefs.getBoolean(SettingsActivity.KEY_EXPIRY_ALERTS, true);

        // Rows are reused by RecyclerView, so always reset the colour first
        holder.expiry.setTextColor(holder.defaultExpiryColor);

        String expiryDate = item.getExpiryDate();
        if (expiryDate == null || expiryDate.isEmpty()) {
            holder.expiry.setText("No expiry date");
        } else {
            long days = daysUntil(expiryDate);
            if (days < 0) {
                holder.expiry.setText("Expired on " + expiryDate);
            } else if (days == 0) {
                holder.expiry.setText("Expires today");
            } else {
                holder.expiry.setText("Expires: " + expiryDate + " (" + days + " days)");
            }
            if (alertsOn && days <= EXPIRING_SOON_DAYS) {
                holder.expiry.setTextColor(Color.RED);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    // Whole days from today until the given yyyy-MM-dd date (negative = already expired)
    private long daysUntil(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date expiry = format.parse(date);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diff = expiry.getTime() - today.getTimeInMillis();
            return Math.round(diff / 86400000.0);
        } catch (ParseException e) {
            return Long.MAX_VALUE; // unreadable date: never highlight
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;
        ColorStateList defaultExpiryColor;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textItemName);
            quantity = itemView.findViewById(R.id.textItemQuantity);
            expiry = itemView.findViewById(R.id.textItemExpiry);
            defaultExpiryColor = expiry.getTextColors();
        }
    }
}