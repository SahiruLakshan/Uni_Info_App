package com.example.uniinfoapp;

import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private TextView tvLoginPrompt;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AlertDialog loadingDialog;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeFirebase();

        initViews();

        setClickListeners();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        Log.d(TAG, "Firebase initialized successfully");
    }

    private void initViews() {
        etUsername = findViewById(R.id.signup_username);
        etEmail = findViewById(R.id.signup_email);
        etPassword = findViewById(R.id.signup_password);
        etConfirmPassword = findViewById(R.id.signup_confirm_password);
        btnSignUp = findViewById(R.id.signin_button);
        tvLoginPrompt = findViewById(R.id.login_prompt);
    }

    private void setClickListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        tvLoginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login activity
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleSignUp() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(username, email, password, confirmPassword)) {
            return;
        }

        showLoadingDialog();

        Log.d(TAG, "Starting user registration process...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                Log.d(TAG, "User created with UID: " + user.getUid());
                                saveUserDataToFirestore(user.getUid(), username, email,password);
                            } else {
                                hideLoadingDialog();
                                Log.e(TAG, "User is null after successful creation");
                                Toast.makeText(SignUpActivity.this,
                                        "Registration failed: User data is null",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            hideLoadingDialog();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            String errorMessage = "Registration failed. ";
                            if (task.getException() != null) {
                                String exceptionMessage = task.getException().getMessage();
                                Log.e(TAG, "Firebase Auth Error: " + exceptionMessage);
                                errorMessage += exceptionMessage;
                            }

                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        boolean isValid = true;

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            isValid = false;
        } else if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            isValid = false;
        } else if (username.length() > 30) {
            etUsername.setError("Username must be less than 30 characters");
            etUsername.requestFocus();
            isValid = false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            if (isValid) etEmail.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            if (isValid) etEmail.requestFocus();
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            if (isValid) etPassword.requestFocus();
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void saveUserDataToFirestore(String userId, String username, String email,String password) {
        Log.d(TAG, "Saving user data to Firestore for UID: " + userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", userId);
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("createdAt", System.currentTimeMillis());
        userData.put("isActive", true);
        userData.put("profileComplete", false);
        userData.put("lastLoginAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoadingDialog();
                        Log.d(TAG, "User data saved successfully to Firestore");
                        Log.d(TAG, "Document created at: users/" + userId);

                        showSuccessDialog(username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoadingDialog();
                        Log.w(TAG, "Error saving user data to Firestore", e);

                        Toast.makeText(SignUpActivity.this,
                                "Account created but failed to save profile data: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                        navigateToNewsActivity();
                    }
                });
    }

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding loading dialog", e);
        }
    }

    private void showSuccessDialog(String username) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView tvWelcome = dialogView.findViewById(R.id.tv_welcome_message);
            tvWelcome.setText("Welcome " + username + "!\nRegistration successful!");

            MaterialButton btnOK = dialogView.findViewById(R.id.btn_dialog_ok);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    navigateToNewsActivity();
                }
            });

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing success dialog", e);
            navigateToNewsActivity();
        }
    }

    private void navigateToNewsActivity() {
        try {
            Intent intent = new Intent(SignUpActivity.this, NewsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to NewsActivity", e);
            Toast.makeText(this, "Registration successful! Please restart the app.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}