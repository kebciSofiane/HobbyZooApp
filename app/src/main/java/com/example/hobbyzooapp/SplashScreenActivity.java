package com.example.hobbyzooapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.AccountManagement.RegistrationOrConnexion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2000; // Durée de l'écran de démarrage (2 secondes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    startActivity(new Intent(SplashScreenActivity.this, WeeklyEvent.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, RegistrationOrConnexion.class));
                }
                finish();
            }
        }, SPLASH_DURATION);
    }
}

