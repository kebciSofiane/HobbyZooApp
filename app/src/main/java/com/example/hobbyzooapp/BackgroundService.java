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

import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.example.hobbyzooapp.Sessions.MyDailySessionsAdapter;
import com.example.hobbyzooapp.Sessions.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class BackgroundService extends Service {



    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "session_channel_id";
    private static final String CHANNEL_NAME = "Session Notifications";

    private Handler handler;
    private Runnable runnable;


    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                // Vérifier si l'heure actuelle est 10 heures du matin
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
               Log.d("abcd" , "" +currentHour);

                if (currentHour == 16) {
                    // Votre logique pour vérifier le nombre de sessions du jour
                    int sessionCount = getSessions().size();

                    Log.d("count", "" + sessionCount);
;                   if (sessionCount > 0) {
                        // Envoyer la notification
                        showSessionNotification(sessionCount);
                    }
                }

                // Planifier la prochaine vérification pour le lendemain à 10 heures
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
                handler.postDelayed(this, delayMillis);
            }
        };
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ArrayList<Session> getSessions() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<Session> mySessions = new ArrayList<>();

        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_image = snapshot.child("session_picture").getValue(String.class);
                    String session_done = snapshot.child("session_done").getValue(String.class);

                    DatabaseReference referenceActivity = database.getReference("Activity");

                    referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String activityName = dataSnapshot.child("activity_name").getValue(String.class).replace(",", " ");
                                int hourDuration = Integer.parseInt(session_duration) / 60;
                                int minutesDuration = Integer.parseInt(session_duration) % 60;
                                String mnemonicPet = dataSnapshot.child("activity_pet").getValue(String.class);
                                mySessions.add(new Session(session_id,
                                        activity_id,
                                        activityName,
                                        new Time(hourDuration, minutesDuration, 0),
                                        Integer.parseInt(session_day),
                                        Integer.parseInt(session_month),
                                        Integer.parseInt(session_year),
                                        session_image,
                                        mnemonicPet,
                                        session_done)
                                );
                            } else {
                                // L'activité n'existe pas dans la base de données
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Une erreur s'est produite lors de la récupération des données
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

        // Attendre la fin de l'exécution de la requête Firebase avant de retourner les sessions
        try {
            Thread.sleep(2000); // Attendre 2 secondes (ajustez selon vos besoins)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("sess" , "" +mySessions);

        return mySessions;
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
