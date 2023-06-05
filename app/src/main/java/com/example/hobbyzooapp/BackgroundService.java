package com.example.hobbyzooapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.hobbyzooapp.Sessions.Session;
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startForeground(NOTIFICATION_ID, notification);

        // Utiliser un indicateur pour vérifier si une notification a déjà été envoyée
        final boolean[] notificationSent = {false};

        getSessions(new SessionsLoadedListener() {
            @Override
            public void onSessionsLoaded(ArrayList<String> sessions) {
                Log.d("BackgroundService", "Sessions loaded: " + sessions.size());
                if (sessions.size() > 0 && !notificationSent[0]) {
                    showSessionNotification(sessions.size());
                    notificationSent[0] = true; // Mettre l'indicateur à true pour éviter l'envoi répété
                }
            }
        });

        handler = new Handler();
        runnable = () -> {
            calendar = Calendar.getInstance();

            if (calendar.get(Calendar.HOUR_OF_DAY) == 18 && calendar.get(Calendar.MINUTE) == 44 && !notificationSent[0]) {
                getSessions(new SessionsLoadedListener() {
                    @Override
                    public void onSessionsLoaded(ArrayList<String> sessions) {
                        Log.d("BackgroundService", "Sessions loaded: " + sessions.size());
                        if (sessions.size() > 0) {
                            showSessionNotification(sessions.size());
                            notificationSent[0] = true; // Mettre l'indicateur à true pour éviter l'envoi répété
                            // Arrêter la planification de la prochaine vérification
                            handler.removeCallbacks(runnable);
                        }
                    }
                });
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 26);
            calendar.set(Calendar.SECOND, 0);

            Intent notificationIntent = new Intent(BackgroundService.this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(BackgroundService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        };
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
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);

                    if (localDate.getDayOfMonth() == Integer.parseInt(session_day) && localDate.getYear() == Integer.parseInt(session_year) && localDate.getMonth().getValue() == Integer.parseInt(session_month)) {
                        mySessions.add("cc");
                        Log.d("", "booom    : " + mySessions);
                    }
                }

                listener.onSessionsLoaded(mySessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void showSessionNotification(int sessionCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startService(intent);
        handler.post(runnable);
        return START_STICKY;
    }

    public interface SessionsLoadedListener {
        void onSessionsLoaded(ArrayList<String> sessions);
    }
}
