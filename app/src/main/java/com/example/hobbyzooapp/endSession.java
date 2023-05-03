package com.example.hobbyzooapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class endSession extends AppCompatActivity {



    ImageView petPic;
    Button takeApic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);
        petPic = findViewById(R.id.petPicture);
        petPic.setImageResource(R.drawable.koala);
        takeApic=findViewById(R.id.takeAPic);


    }
}