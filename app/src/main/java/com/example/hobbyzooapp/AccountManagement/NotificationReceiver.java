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
        //showSessionNotification(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSessionNotification(Context context) {
        //BackgroundService backgroundService = new BackgroundService();
        //backgroundService.showSessionNotification(0);
    }
}