package com.example.hobbyzooapp.AccountManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.hobbyzooapp.AccountManagement.BackgroundService;

public class NotificationReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        int sessionCount = intent.getIntExtra(BackgroundService.EXTRA_SESSION_COUNT, 0);
        // Utilisez un Intent pour démarrer le Service
        Intent serviceIntent = new Intent(context, BackgroundService.class);
        serviceIntent.putExtra(BackgroundService.EXTRA_SESSION_COUNT, sessionCount);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
