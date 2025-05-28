package com.example.uniinfoapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;

public class UserInfoActivity extends AppCompatActivity {

    private AppCompatButton btnEditInfo;
    private AppCompatButton btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSignOut = findViewById(R.id.btnSignOut);
    }

    private void setClickListeners() {
        // Edit Info button click listener
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDetailsDialog();
            }
        });

        // Sign Out button click listener (optional)
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign out logic
                Toast.makeText(UserInfoActivity.this, "Sign Out clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDetailsDialog() {
        // Create AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        // Create and configure the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get references to dialog views
        TextInputEditText etUsername = dialogView.findViewById(R.id.etDialogUsername);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etDialogEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etDialogPassword);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btnDialogSave);
        AppCompatButton btnClose = dialogView.findViewById(R.id.btnDialogClose);

        // Set current user data (you can get this from SharedPreferences, database, etc.)
        etUsername.setText("Lakshan");
        etEmail.setText("lakshan@gmail.com");
        etPassword.setText(""); // Leave empty for security

        // Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validation
                if (username.isEmpty()) {
                    etUsername.setError("Username is required");
                    return;
                }

                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    return;
                }

                if (!isValidEmail(email)) {
                    etEmail.setError("Please enter a valid email");
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters");
                    return;
                }

                // Update user info
                updateUserInfo(username, email, password);

                // Show success message
                Toast.makeText(UserInfoActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                // Close dialog
                dialog.dismiss();
            }
        });

        // Close button click listener
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void updateUserInfo(String username, String email, String password) {
        // TODO: Implement your update logic here
        // This could involve:
        // 1. Updating SharedPreferences
        // 2. Updating database
        // 3. Making API call to server
        // 4. Updating UI elements on the main page

        // Example: Update SharedPreferences
        /*
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("password", password); // Note: Never store plain text passwords in real apps
        editor.apply();
        */

        // Example: Update UI
        // updateUserInfoDisplay(username, email);
    }

    private void updateUserInfoDisplay(String username, String email) {
        // Update the TextViews in your user info card
        // You'll need to add IDs to your TextViews first
        /*
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvUserEmail);

        tvUsername.setText("Username : " + username);
        tvEmail.setText("Email : " + email);
        */
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}