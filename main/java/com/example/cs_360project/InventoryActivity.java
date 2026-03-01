package com.example.cs_360project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private DBHelper db;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = new DBHelper(this);

        RecyclerView rv = findViewById(R.id.rvInventory);
        rv.setLayoutManager(new GridLayoutManager(this, 2)); // grid
        adapter = new InventoryAdapter(new InventoryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(InventoryAdapter.Item item) {
                showEditDialog(item);
            }

            @Override
            public void onItemLongClick(InventoryAdapter.Item item) {
                showDeleteDialog(item);
            }
        });
        rv.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddDialog());

        Button btnSms = findViewById(R.id.btnSmsSettings);
        btnSms.setOnClickListener(v -> startActivity(new Intent(this, SmsSettingsActivity.class)));

        refreshGrid();
    }

    private void refreshGrid() {
        ArrayList<InventoryAdapter.Item> items = new ArrayList<>();
        Cursor c = db.getAllItems();
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String name = c.getString(1);
            int qty = c.getInt(2);
            items.add(new InventoryAdapter.Item(id, name, qty));
        }
        c.close();
        adapter.setItems(items);
    }

    private void showAddDialog() {
        EditText etName = new EditText(this);
        etName.setHint("Item name");

        EditText etQty = new EditText(this);
        etQty.setHint("Quantity");
        etQty.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);
        layout.addView(etName);
        layout.addView(etQty);

        new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    int qty = parseIntSafe(etQty.getText().toString().trim());
                    if (!name.isEmpty()) {
                        db.addItem(name, qty);
                        refreshGrid();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(InventoryAdapter.Item item) {
        EditText etName = new EditText(this);
        etName.setText(item.name);

        EditText etQty = new EditText(this);
        etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        etQty.setText(String.valueOf(item.qty));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);
        layout.addView(etName);
        layout.addView(etQty);

        new AlertDialog.Builder(this)
                .setTitle("Update Item")
                .setView(layout)
                .setPositiveButton("Update", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    int qty = parseIntSafe(etQty.getText().toString().trim());
                    if (!name.isEmpty()) {
                        db.updateItem(item.id, name, qty);
                        refreshGrid();
                        SmsUtil.maybeSendLowInventoryAlert(this, name, qty); // SMS trigger
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog(InventoryAdapter.Item item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Remove " + item.name + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteItem(item.id);
                    refreshGrid();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
}