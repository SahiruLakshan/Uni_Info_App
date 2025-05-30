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

        initViews();

        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProfile = findViewById(R.id.ivProfile);
        btnSeemore = findViewById(R.id.btnSeemore);
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        btnSeemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExamRegistrationDialog();
            }
        });
    }

    private void showExamRegistrationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.news_more);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

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

        dialog.show();
    }
}