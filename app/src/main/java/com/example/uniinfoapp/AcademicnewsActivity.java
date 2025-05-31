package com.example.uniinfoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AcademicnewsActivity extends AppCompatActivity {
    private ImageView ivBack;
    private ImageView ivProfile;
    private LinearLayout academicBtn,eventBtn,sportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic);

        initViews();

        setClickListeners();
    }

    private void initViews() {
        academicBtn = findViewById(R.id.academic_btn);
        eventBtn = findViewById(R.id.event_btn);
        sportBtn = findViewById(R.id.sport_btn);
        ivBack = findViewById(R.id.ivBack);
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcademicnewsActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcademicnewsActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        academicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcademicnewsActivity.this, AcademicnewsActivity.class);
                startActivity(intent);
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcademicnewsActivity.this, EventnewsActivity.class);
                startActivity(intent);
            }
        });

        sportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcademicnewsActivity.this, SportnewsActivity.class);
                startActivity(intent);
            }
        });

    }
}
