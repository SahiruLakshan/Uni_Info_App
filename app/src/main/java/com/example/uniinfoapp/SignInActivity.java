package com.example.uniinfoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignInActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button signInButton;
    TextView registerPrompt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.signin_username);
        passwordEditText = findViewById(R.id.signin_password);
        signInButton = findViewById(R.id.signin_button);
        registerPrompt = findViewById(R.id.register_prompt);

        signInButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the login attempt
            Log.d("SignInActivity", "Attempting login with email: " + email);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Log.d("SignInActivity", "Firebase Auth successful");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Log.d("SignInActivity", "User UID: " + uid);

                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            Log.d("SignInActivity", "User document found in Firestore");
                                            Boolean isActive = documentSnapshot.getBoolean("isActive");
                                            Log.d("SignInActivity", "User isActive: " + isActive);

                                            if (isActive != null && isActive) {
                                                // Update lastLoginAt timestamp
                                                db.collection("users").document(uid)
                                                        .update("lastLoginAt", System.currentTimeMillis())
                                                        .addOnSuccessListener(aVoid -> Log.d("SignInActivity", "Last login time updated"))
                                                        .addOnFailureListener(e -> Log.e("SignInActivity", "Failed to update last login time", e));

                                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, NewsActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Account is inactive. Please contact support.", Toast.LENGTH_LONG).show();
                                                mAuth.signOut(); // Sign out if account is inactive
                                            }
                                        } else {
                                            Log.e("SignInActivity", "User document not found in Firestore");
                                            Toast.makeText(this, "User profile not found. Please contact support.", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SignIn", "Firestore error", e);
                                        Toast.makeText(this, "Error accessing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SignInActivity", "Firebase Auth failed", e);
                        String errorMessage = "Login failed: ";

                        // Provide more specific error messages
                        if (e.getMessage() != null) {
                            if (e.getMessage().contains("password is invalid")) {
                                errorMessage += "Incorrect password. Please try again.";
                            } else if (e.getMessage().contains("no user record")) {
                                errorMessage += "No account found with this email.";
                            } else if (e.getMessage().contains("email address is badly formatted")) {
                                errorMessage += "Please enter a valid email address.";
                            } else if (e.getMessage().contains("network error")) {
                                errorMessage += "Network error. Please check your connection.";
                            } else {
                                errorMessage += e.getMessage();
                            }
                        } else {
                            errorMessage += "Unknown error occurred.";
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    });
        });

        registerPrompt.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("SignInActivity", "User already signed in: " + currentUser.getEmail());
            // Optionally redirect to main activity if user is already logged in
            // startActivity(new Intent(this, NewsActivity.class));
            // finish();
        }
    }
}