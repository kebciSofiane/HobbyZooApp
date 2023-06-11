package com.example.hobbyzooapp.AccountManagement;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        handler = new Handler();
        runnable = () -> {
            calendar = Calendar.getInstance();

            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            if (currentHour == 15 && currentMinute == 28 && !notificationSent) {

                getSessions(sessions -> {
                    int sessionCount = sessions.size();
                    if (sessionCount > 0) {
                        showSessionNotification(sessionCount);
                    }
                    notificationSent = true;
                });
            } else {
                notificationSent = false;
            }

            handler.postDelayed(runnable, 60000);
        };

        handler.post(runnable);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 26);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);

                    assert session_day != null;
                    if (localDate.getDayOfMonth() == Integer.parseInt(session_day)) {
                        assert session_month != null;
                        if (localDate.getMonthValue() == Integer.parseInt(session_month)) {
                            assert session_year != null;
                            if (localDate.getYear() == Integer.parseInt(session_year)) {
                                mySessions.add(snapshot.getKey());
                            }
                        }
                    }
                }

                listener.onSessionsLoaded(mySessions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("BackgroundService", "Failed to load sessions from Firebase", databaseError.toException());
            }
        });
    }

    void showSessionNotification(int sessionCount) {
        String notificationTitle = "Reminder";
        String notificationText = "You have " + sessionCount + " sessions today.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private interface SessionsLoadedListener {
        void onSessionsLoaded(ArrayList<String> sessions);
    }
}
