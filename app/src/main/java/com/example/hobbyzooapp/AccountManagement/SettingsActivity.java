package com.example.hobbyzooapp.AccountManagement;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    public static final String NOTIFICATION_ENABLED_KEY = "notification_enabled";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    FirebaseAuth firebaseAuth;

    private Button notificationsEnabledButton, notificationsDisabledButton, termsButton, helpButton, aboutButton, logoutButton;
    private ImageButton backButton;
    private int activeIcon, inactiveIcon;

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
                Toast.makeText(getBaseContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
                cancelNotification();
                saveNotificationEnabledState(false);
            }
        });

        notificationsDisabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsDisabledButton.setVisibility(View.GONE);
                notificationsEnabledButton.setVisibility(View.VISIBLE);
                saveNotificationEnabledState(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Vérifier si la permission de notification est accordée
                    if (NotificationManagerCompat.from(SettingsActivity.this).areNotificationsEnabled()) {
                        Toast.makeText(getBaseContext(), " Notifications enabled : \n You will now receive notifications.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Demander la permission de notification à l'utilisateur
                        requestNotificationPermission();
                    }
                } else {
                    showNotification("Notifications enabled", "You will now receive notifications.");
                }
            }
        });


        termsButton.setOnClickListener(v -> {
            // Gérer l'action du bouton Terms
        });

        helpButton.setOnClickListener(v -> {
            // Gérer l'action du bouton Help
        });

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

    private void requestNotificationPermission() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());

        // Vérifier si l'utilisateur a déjà été redirigé vers les paramètres de notification
        if (isNotificationPermissionRequested()) {
            // Redemander la permission
            startActivity(intent);
        } else {
            // Marquer la demande de permission de notification comme effectuée
            markNotificationPermissionRequested();

            // Demander à l'utilisateur de donner la permission de notification
            Toast.makeText(this, "Please enable notification permission", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    private boolean isNotificationPermissionRequested() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("notification_permission_requested", false);
    }

    private void markNotificationPermissionRequested() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_permission_requested", true);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Vérifier à nouveau si la permission de notification est accordée
                if (NotificationManagerCompat.from(SettingsActivity.this).areNotificationsEnabled()) {
                    showNotification("Notifications enabled", "You will now receive notifications.");
                } else {
                    // L'utilisateur n'a toujours pas donné la permission de notification
                    Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
