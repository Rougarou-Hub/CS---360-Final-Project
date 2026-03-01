package com.example.cs_360project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnCreateAccount;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);   // Make sure this matches your XML name

        db = new DBHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();

            if (db.validateLogin(u, p)) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateAccount.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();

            if (db.userExists(u)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else if (db.createUser(u, p)) {
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}