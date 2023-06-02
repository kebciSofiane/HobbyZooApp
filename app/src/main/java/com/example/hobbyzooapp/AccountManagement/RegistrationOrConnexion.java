package com.example.hobbyzooapp.AccountManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.R;

public class RegistrationOrConnexion extends AppCompatActivity {

    Button registerBtn,loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationorconnexion);

        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this, RegisterActivity.class));

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this, LoginActivity.class));

            }
        });
    }
}