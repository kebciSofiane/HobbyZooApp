package com.example.hobbyzooapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private Button notificationsButton, termsButton, rateButton, helpButton, aboutButton, logoutButton;
    private ImageButton backBtn;
    private int activeIcon, inactiveIcon;
    private boolean isNotificationsEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsButton = findViewById(R.id.notificationsBtn);
        termsButton = findViewById(R.id.termsBtn);
//        rateButton = findViewById(R.id.rateBtn);
        helpButton = findViewById(R.id.helpBtn);
        aboutButton = findViewById(R.id.aboutBtn);
        logoutButton = findViewById(R.id.logoutBtn);
        backBtn = findViewById(R.id.backButton);
        activeIcon = R.drawable.ic_notifications_active;
        inactiveIcon = R.drawable.ic_notifications_off;

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inverser l'état des notifications
                isNotificationsEnabled = !isNotificationsEnabled;

                if (isNotificationsEnabled) {
                    // Activer les notifications
                    notificationsButton.setText("Disable notifications");
                    showNotification("Notifications enabled", "You will now receive notifications.");
                    notificationsButton.setCompoundDrawablesWithIntrinsicBounds(activeIcon, 0, 0, 0);
                } else {
                    // Désactiver les notifications
                    notificationsButton.setText("Enable notifications");
                    cancelNotification();
                    notificationsButton.setCompoundDrawablesWithIntrinsicBounds(inactiveIcon, 0, 0, 0);
                }
            }
        });



        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logique pour gérer le clic sur le bouton "Terms of Service"
            }
        });
//
//        rateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Logique pour gérer le clic sur le bouton "Rate HobbyZoo"
//            }
//        });

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));

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

    private void showNotification(String title, String message) {
        // Vérifier si le canal de notification existe déjà (pour les versions Android Oreo et supérieures)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Créer la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Afficher la notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, builder.build());
    }
//todo : the icon doesn't change at the first click
    private void cancelNotification() {
        // Annuler la notification avec l'ID spécifié
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancel(1);

        // Mettre à jour l'icône du bouton pour l'état des notifications désactivé
        notificationsButton.setCompoundDrawablesWithIntrinsicBounds(inactiveIcon, 0, 0, 0);
    }

}