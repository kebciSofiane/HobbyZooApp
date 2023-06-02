package com.example.hobbyzooapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    Calendar calendar;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
//        getSessions();
//
        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                // Vérifier si l'heure actuelle est 10 heures du matin
                calendar = Calendar.getInstance();
                getSessions();
                Log.d("abcd" , "" );


            }
        };

        calendar = Calendar.getInstance();  // Initialiser la variable calendar ici
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        handler.postDelayed(runnable, delayMillis);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> getSessions() {
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

                    if(localDate.getDayOfMonth() == Integer.parseInt(session_day) && localDate.getYear() == Integer.parseInt(session_year) && localDate.getMonth().getValue() == Integer.parseInt(session_month)){
                        mySessions.add("cc");
                        Log.d("", "booom    : "+ mySessions);
                    }
                }
                calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                if (currentHour == 18 ) {
                    int sessionCount = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        sessionCount = mySessions.size();
                    }
                    Log.d("", "baam    : "+ sessionCount);

                    if (sessionCount > 0) {
                        // Envoyer la notification
                        showSessionNotification(sessionCount);
                    }

                }

                // Planifier la prochaine vérification pour le lendemain à 10 heures



                // Appeler la méthode de rappel pour notifier le chargement des sessions
                //listener.onSessionsLoaded(mySessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
        return mySessions;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void getS(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference referenceActivity = database.getReference("Activity");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<Session> mySessions = new ArrayList<>();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showSessionNotification(int sessionCount) {
        // Vérifier si les notifications sont activées
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
            // Les notifications sont désactivées, vous pouvez choisir de ne pas envoyer de notification ou de prendre une autre action appropriée.
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
            int uniqueNotificationId = (int) System.currentTimeMillis(); // Utilisez un ID unique pour chaque notification
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


}
