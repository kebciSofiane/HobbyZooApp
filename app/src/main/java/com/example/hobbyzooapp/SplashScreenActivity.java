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
                // Vérifiez si l'utilisateur est déjà connecté
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    // Utilisateur connecté, redirigez vers l'écran d'accueil
                    startActivity(new Intent(SplashScreenActivity.this, WeeklyEvent.class));
                } else {
                    // Utilisateur non connecté ou adresse e-mail non vérifiée, redirigez vers l'écran de connexion ou d'inscription
                    startActivity(new Intent(SplashScreenActivity.this, RegistrationOrConnexion.class));
                }
                finish(); // Terminer l'activité actuelle pour empêcher l'utilisateur de revenir en arrière
            }
        }, SPLASH_DURATION);
    }
}

