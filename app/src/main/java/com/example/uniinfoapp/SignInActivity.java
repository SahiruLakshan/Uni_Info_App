package com.example.uniinfoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        usernameEditText = findViewById(R.id.signin_username);
        passwordEditText = findViewById(R.id.signin_password);
        signInButton = findViewById(R.id.signin_button);

        signInButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.equals("admin") && password.equals("1234")) {
                Intent intent = new Intent(SignInActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
