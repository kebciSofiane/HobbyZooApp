package com.example.hobbyzooapp.Sessions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hobbyzooapp.R;

public class MySessionsTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sessions_test);
        TextView dateSession = findViewById(R.id.dateSession);

        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");
        dateSession.setText(date);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}