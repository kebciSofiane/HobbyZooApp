package com.example.hobbyzooapp.AccountManagement;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class BackgroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "session_channel_id";
    private static final String CHANNEL_NAME = "Session Notifications";

    private Handler handler;
    private Runnable runnable;
    private Calendar calendar;
    private boolean notificationSent = false;

    private boolean isNotificationEnabled = true;

    public static final String ACTION_NOTIFICATION_ENABLED = "com.example.hobbyzooapp.ACTION_NOTIFICATION_ENABLED";
    public static final String EXTRA_NOTIFICATION_ENABLED = "com.example.hobbyzooapp.EXTRA_NOTIFICATION_ENABLED";
    public static final String EXTRA_SESSION_COUNT = "com.example.hobbyzooapp.EXTRA_SESSION_COUNT";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        isNotificationEnabled = getNotificationEnabledState();

        Notification notification = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startForeground(NOTIFICATION_ID, notification);

        runnable = () -> {
            handleNotificationLogic();

            handler.postDelayed(runnable, 60000);
        };

        handler.post(runnable);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 2);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean getNotificationEnabledState() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(SettingsActivity.NOTIFICATION_ENABLED_KEY, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNotificationEnabledState(boolean enabled) {
        isNotificationEnabled = enabled;
        // Vous pouvez également mettre à jour l'état de la notification ici si nécessaire
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_NOTIFICATION_ENABLED)) {
                boolean enabled = intent.getBooleanExtra(EXTRA_NOTIFICATION_ENABLED, true);
                updateNotificationEnabledState(enabled);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSessions(SessionsLoadedListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<String> mySessions = new ArrayList<>();
        LocalDate localDate = LocalDate.now();

        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);

                    assert session_day != null;
                    if (localDate.getDayOfMonth() == Integer.parseInt(session_day)) {
                        assert session_year != null;
                        if (localDate.getYear() == Integer.parseInt(session_year) && localDate.getMonth().getValue() == Integer.parseInt(session_month)) {
                            mySessions.add("cc");
                        }
                    }
                }

                listener.onSessionsLoaded(mySessions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleNotificationLogic() {
        calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        if (currentHour == 17 && currentMinute == 51 && !notificationSent && isNotificationEnabled) {
            getSessions(sessions -> {
                int sessionCount = sessions.size();
                if (sessionCount > 0) {
                    showSessionNotification(getApplicationContext(), sessionCount);
                }
                notificationSent = true;
            });
        } else {
            notificationSent = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void showSessionNotification(Context context,int sessionCount) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
            return;
        }

        String title = "Sessions Today";
        String message = "You have " + sessionCount + " sessions scheduled today.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (notificationManager != null) {
            createNotificationChannel(notificationManager);
            int uniqueNotificationId = (int) System.currentTimeMillis();
            notificationManager.notify(uniqueNotificationId, builder.build());
        }
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle("Session Notifications")
                .setContentText("Running in background");

        return builder.build();
    }

    public interface SessionsLoadedListener {
        void onSessionsLoaded(ArrayList<String> sessions);
    }
}
