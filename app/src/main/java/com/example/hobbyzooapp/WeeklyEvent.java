package com.example.hobbyzooapp;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class WeeklyEvent extends AppCompatActivity {

    // ...

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void scheduleWeeklyEvent() {
        // Créer une intention pour le récepteur de diffusion (BroadcastReceiver)
        Intent intent = new Intent(this, WeeklyEventReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Obtenir une instance de l'AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Définir l'heure de début de l'événement hebdomadaire
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 16);

        // Planifier l'événement hebdomadaire
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    // ...
}

