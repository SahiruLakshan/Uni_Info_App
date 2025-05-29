package com.example.uniinfoapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class NewsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivProfile;
    private AppCompatButton btnSeemore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProfile = findViewById(R.id.ivProfile);
        btnSeemore = findViewById(R.id.btnSeemore);
    }

    private void setClickListeners() {
        // Back arrow click listener - navigate to sign in page
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Profile icon click listener - navigate to user info page
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        // See More button click listener - show exam registration dialog
        btnSeemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExamRegistrationDialog();
            }
        });
    }

    private void showExamRegistrationDialog() {
        // Create custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.news_more);
        dialog.setCancelable(true);

        // Make dialog background transparent and set proper size
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        // Find and set up the close button
        try {
            TextView btnClose = dialog.findViewById(R.id.close);
            if (btnClose != null) {
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show the dialog
        dialog.show();
    }
}