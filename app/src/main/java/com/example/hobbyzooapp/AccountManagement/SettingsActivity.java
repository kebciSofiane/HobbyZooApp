package com.example.hobbyzooapp.AccountManagement;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private Button notificationsEnabledButton,notificationsDisabledButton, termsButton, helpButton, aboutButton, logoutButton;
    private ImageButton backButton;
    private int activeIcon, inactiveIcon;


    //
    private static final String NOTIFICATION_ENABLED_KEY = "notification_enabled";
    //
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
        activeIcon = R.drawable.ic_notifications_active;
        inactiveIcon = R.drawable.ic_notifications_off;

//        notificationsEnabledButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notificationsEnabledButton.setVisibility(View.GONE);
//                notificationsDisabledButton.setVisibility(View.VISIBLE);
//                cancelNotification();
//            }
//        });
//
//        notificationsDisabledButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notificationsDisabledButton.setVisibility(View.GONE);
//                notificationsEnabledButton.setVisibility(View.VISIBLE);
//                showNotification("Notifications enabled", "You will now receive notifications.");
//            }
//        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotificationEnabled = sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, true);

        if (isNotificationEnabled) {
            notificationsEnabledButton.setVisibility(View.GONE);
            notificationsDisabledButton.setVisibility(View.VISIBLE);
        } else {
            notificationsDisabledButton.setVisibility(View.GONE);
            notificationsEnabledButton.setVisibility(View.VISIBLE);
        }

        notificationsEnabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsEnabledButton.setVisibility(View.GONE);
                notificationsDisabledButton.setVisibility(View.VISIBLE);
                cancelNotification();
                saveNotificationEnabledState(false);
            }
        });

        notificationsDisabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsDisabledButton.setVisibility(View.GONE);
                notificationsEnabledButton.setVisibility(View.VISIBLE);
                showNotification("Notifications enabled", "You will now receive notifications.");
                saveNotificationEnabledState(true);
            }
        });

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
                finishAffinity();
                finish();
            }
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

    private void saveNotificationEnabledState(boolean enabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_ENABLED_KEY, enabled);
        editor.apply();
    }
    @Override
    public void onBackPressed() {
        saveNotificationEnabledState(notificationsDisabledButton.getVisibility() == View.VISIBLE);
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        saveNotificationEnabledState(notificationsDisabledButton.getVisibility() == View.VISIBLE);
        super.onPause();
    }

}