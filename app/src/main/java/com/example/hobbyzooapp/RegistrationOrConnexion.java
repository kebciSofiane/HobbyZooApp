package com.example.hobbyzooapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationOrConnexion extends AppCompatActivity {

    //todo to complete - make link between front and back + lik with the registration activity

    Button registerBtn,loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);


        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationOrConnexion.this,RegisterActivity.class));

            }
        });
    }
}