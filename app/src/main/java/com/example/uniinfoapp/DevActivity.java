package com.example.uniinfoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DevActivity extends AppCompatActivity {
    private ImageView ivBack;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_info);

        initViews();

        setClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void setClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

    }
}
