package com.example.hobbyzooapp;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class WeeklyEvent extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Obtenez une instance de l'AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 50);

        // Vérifiez si la date programmée est déjà passée, sinon ajoutez 7 jours
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Créez une intention pour votre AlarmReceiver
        Intent intent = new Intent(this, WeeklyEventReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

         // Planifiez l'alarme récurrente tous les lundis à l'heure spécifiée
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        //Suppresion de l'alarme;
        //alarmManager.cancel(pendingIntent);
        finish();

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void scheduleWeeklyEvent() {
        // Créer une intention pour le récepteur de diffusion (BroadcastReceiver)
        Intent intent = new Intent(this, WeeklyEventReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Obtenir une instance de l'AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);



        // Planifier l'événement hebdomadaire
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
         //       AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    // ...
}

