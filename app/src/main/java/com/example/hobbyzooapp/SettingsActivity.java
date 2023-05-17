package com.example.hobbyzooapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private Button notificationsButton;
    private Button termsButton;
    private Button rateButton;
    private Button helpButton;
    private Button aboutButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsButton = findViewById(R.id.notificationsBtn);
        termsButton = findViewById(R.id.termsBtn);
        rateButton = findViewById(R.id.rateBtn);
        helpButton = findViewById(R.id.helpBtn);
        aboutButton = findViewById(R.id.aboutBtn);
        logoutButton = findViewById(R.id.logoutBtn);

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "Notifications"
            }
        });
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "Terms of Service"
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "Rate HobbyZoo"
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "Help"
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "About"
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });

    }
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Utilisateur connecté, redirigez-le vers HomeActivity
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        } else {
            // Utilisateur non connecté, redirigez-le vers RegistrationOrConnexion
            startActivity(new Intent(SettingsActivity.this, RegistrationOrConnexion.class));
        }
        finish();
    }

}
