package com.example.hobbyzooapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationOrConnexion extends AppCompatActivity {

    Button registerBtn,loginBtn, home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationorconnexion);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main");

        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);
        home = findViewById(R.id.Home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this,HomeActivity.class));

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this,RegisterActivity.class));

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this,LoginActivity.class));

            }
        });
    }
}