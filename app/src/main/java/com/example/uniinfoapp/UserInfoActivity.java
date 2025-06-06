package com.example.uniinfoapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private AppCompatButton btnEditInfo;
    private AppCompatButton btnSignOut;
    private TextView tvUsername, tvEmail, tvUserID;
    private ImageView ivBack;
    private TextView tvDeleteProfile,tvDevInfo;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String currentUserId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            Log.d("UserInfoActivity", "Current User ID: " + currentUserId);
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        loadUserData();

        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnSignOut = findViewById(R.id.btnSignOut);
        tvDeleteProfile = findViewById(R.id.tvDeleteProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvUserEmail);
        tvUserID = findViewById(R.id.tvUserPassword);
        tvDevInfo = findViewById(R.id.tvDevInfo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void loadUserData() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Loading user data...");
        progressDialog.show();

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            String email = document.getString("email");

                            tvUsername.setText("Username: " + (username != null ? username : "Not set"));
                            tvEmail.setText("Email: " + (email != null ? email : currentUser.getEmail()));
                            tvUserID.setText("User ID: " + currentUserId);

                            Log.d("UserInfoActivity", "User data loaded successfully");
                        } else {

                            tvEmail.setText("Email: " + currentUser.getEmail());
                            tvUsername.setText("Username: Not set");
                            tvUserID.setText("User ID: " + currentUserId);
                            Log.d("UserInfoActivity", "No document found for user");
                        }
                    } else {
                        Toast.makeText(UserInfoActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        Log.e("UserInfoActivity", "Error getting document: ", task.getException());
                    }
                });
    }

    private void setClickListeners() {
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDetailsDialog();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });

        tvDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteProfileConfirmation();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        tvDevInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, DevActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDeleteProfileConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure you want to delete your profile? This action cannot be undone and all your data will be permanently removed.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("DELETE", (dialog, which) -> {
            showPasswordConfirmationForDelete();
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private void showPasswordConfirmationForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(android.R.layout.select_dialog_item, null);

        final android.widget.EditText passwordInput = new android.widget.EditText(this);
        passwordInput.setHint("Enter your password to confirm");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setPadding(50, 30, 50, 30);

        builder.setTitle("Confirm Password");
        builder.setMessage("Please enter your current password to delete your profile:");
        builder.setView(passwordInput);

        builder.setPositiveButton("CONFIRM DELETE", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            }
            deleteUserProfile(password);
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private void deleteUserProfile(String password) {
        progressDialog.setMessage("Deleting profile...");
        progressDialog.show();

        String email = currentUser.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                db.collection("users").document(currentUserId)
                        .delete()
                        .addOnCompleteListener(firestoreTask -> {
                            if (firestoreTask.isSuccessful()) {
                                currentUser.delete().addOnCompleteListener(deleteTask -> {
                                    progressDialog.dismiss();

                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(UserInfoActivity.this, "Profile deleted successfully", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(UserInfoActivity.this, SignInActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                        Log.d("UserInfoActivity", "User profile deleted successfully");
                                    } else {
                                        Toast.makeText(UserInfoActivity.this, "Failed to delete profile: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("UserInfoActivity", "Failed to delete Firebase Auth user", deleteTask.getException());
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UserInfoActivity.this, "Failed to delete user data: " + firestoreTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("UserInfoActivity", "Failed to delete Firestore document", firestoreTask.getException());
                            }
                        });
            } else {
                progressDialog.dismiss();
                Toast.makeText(UserInfoActivity.this, "Authentication failed. Please check your password.", Toast.LENGTH_LONG).show();
                Log.e("UserInfoActivity", "Re-authentication failed", reauthTask.getException());
            }
        });
    }

    private void showEditDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText etUsername = dialogView.findViewById(R.id.etDialogUsername);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etDialogEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etDialogPassword);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btnDialogSave);
        AppCompatButton btnClose = dialogView.findViewById(R.id.btnDialogClose);

        loadCurrentUserDataIntoDialog(etUsername, etEmail, etPassword);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

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

                updateUserInfo(username, email, password, dialog);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadCurrentUserDataIntoDialog(TextInputEditText etUsername, TextInputEditText etEmail, TextInputEditText etPassword) {
        if (currentUser == null) return;

        etEmail.setText(currentUser.getEmail());

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            String password = document.getString("password");

                            if (username != null) {
                                etUsername.setText(username);
                            }

                            // Show the stored password in the dialog
                            if (password != null) {
                                etPassword.setText(password);
                            }
                        }
                    } else {
                        Log.e("UserInfoActivity", "Failed to load user data for dialog", task.getException());
                    }
                });
    }

    private void updateUserInfo(String username, String email, String password, AlertDialog dialog) {
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        String currentEmail = currentUser.getEmail();
        boolean emailChanged = !email.equals(currentEmail);

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String currentStoredPassword = document.getString("password");

                            if (currentStoredPassword != null) {
                                performUserUpdates(username, email, password, currentStoredPassword, emailChanged, dialog);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UserInfoActivity.this, "Current password not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfoActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UserInfoActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performUserUpdates(String username, String email, String newPassword, String currentPassword, boolean emailChanged, AlertDialog dialog) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", email);
        userUpdates.put("password", newPassword);
        userUpdates.put("lastUpdated", System.currentTimeMillis());

        if (emailChanged) {
            String currentEmail = currentUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);

            currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                if (reauthTask.isSuccessful()) {
                    currentUser.updateEmail(email).addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            updatePasswordAndFirestore(newPassword, userUpdates, dialog);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfoActivity.this, "Failed to update email: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "Re-authentication failed. Please check your current password.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            String currentEmail = currentUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);

            currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                if (reauthTask.isSuccessful()) {
                    updatePasswordAndFirestore(newPassword, userUpdates, dialog);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "Re-authentication failed. Please check your current password.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updatePasswordAndFirestore(String newPassword, Map<String, Object> userUpdates, AlertDialog dialog) {
        currentUser.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
            if (passwordTask.isSuccessful()) {
                db.collection("users").document(currentUserId)
                        .update(userUpdates)
                        .addOnCompleteListener(firestoreTask -> {
                            progressDialog.dismiss();

                            if (firestoreTask.isSuccessful()) {
                                Toast.makeText(UserInfoActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                loadUserData(); // Refresh UI
                                dialog.dismiss();
                            } else {
                                Toast.makeText(UserInfoActivity.this, "Profile updated in authentication but failed to save to database", Toast.LENGTH_LONG).show();
                                Log.e("UserInfoActivity", "Firestore update failed", firestoreTask.getException());
                            }
                        });
            } else {
                progressDialog.dismiss();
                Toast.makeText(UserInfoActivity.this, "Failed to update password: " + passwordTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signOutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            mAuth.signOut();
            Toast.makeText(UserInfoActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserInfoActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}