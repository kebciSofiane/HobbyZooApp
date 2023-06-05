package com.example.hobbyzooapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NotificationReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Votre code pour afficher la notification ici
        showSessionNotification(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSessionNotification(Context context) {
        // Votre code pour afficher la notification ici
        // Utilisez le code de la méthode showSessionNotification() de la classe BackgroundService
        BackgroundService backgroundService = new BackgroundService();
        backgroundService.showSessionNotification(0);
    }
}
