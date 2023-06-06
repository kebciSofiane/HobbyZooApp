package com.example.hobbyzooapp.AccountManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private Button notificationsEnabledButton,notificationsDisabledButton, termsButton, helpButton, aboutButton, logoutButton;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsEnabledButton = findViewById(R.id.notificationsEnabledBtn);
        notificationsDisabledButton = findViewById(R.id.notificationsDisabledBtn);
        termsButton = findViewById(R.id.termsBtn);
        helpButton = findViewById(R.id.helpBtn);
        aboutButton = findViewById(R.id.aboutBtn);
        logoutButton = findViewById(R.id.logoutBtn);
        backButton = findViewById(R.id.backButton);

        notificationsEnabledButton.setOnClickListener(v -> {
            notificationsEnabledButton.setVisibility(View.GONE);
            notificationsDisabledButton.setVisibility(View.VISIBLE);
            cancelNotification();
        });

        notificationsDisabledButton.setOnClickListener(v -> {
            notificationsDisabledButton.setVisibility(View.GONE);
            notificationsEnabledButton.setVisibility(View.VISIBLE);
            showNotification("Notifications enabled", "You will now receive notifications.");
        });

        termsButton.setOnClickListener(v -> {});

        helpButton.setOnClickListener(v -> {});

        aboutButton.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AboutActivity.class)));

        backButton.setOnClickListener(v -> finish());

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUserStatus();
            finishAffinity();
            finish();
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SettingsActivity.this, RegistrationOrConnexion.class));
        }
        finish();
    }

    private void showNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, builder.build());
    }

    private void cancelNotification() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancel(1);

        notificationsEnabledButton.setVisibility(View.GONE);
        notificationsDisabledButton.setVisibility(View.VISIBLE);
    }

}