package com.example.hobbyzooapp.AccountManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.hobbyzooapp.R;

public class AboutActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

    }


}