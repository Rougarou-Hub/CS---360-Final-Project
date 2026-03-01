package com.example.cs_360project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SmsSettingsActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> permLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_settings);

        Switch swEnable = findViewById(R.id.swSmsEnable);
        EditText etPhone = findViewById(R.id.etPhone);
        Button btnSave = findViewById(R.id.btnSaveSms);

        swEnable.setChecked(SmsUtil.isEnabled(this));
        etPhone.setText(SmsUtil.getPhone(this));

        permLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!granted) {
                        swEnable.setChecked(false);
                        SmsUtil.setEnabled(this, false);
                        Toast.makeText(this, "SMS permission denied. SMS disabled.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        swEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    permLauncher.launch(Manifest.permission.SEND_SMS);
                } else {
                    SmsUtil.setEnabled(this, true);
                }
            } else {
                SmsUtil.setEnabled(this, false);
            }
        });

        btnSave.setOnClickListener(v -> {
            SmsUtil.setPhone(this, etPhone.getText().toString().trim());
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });
    }
}