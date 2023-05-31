package com.example.hobbyzooapp.CalendarEvolution;

interface ActivityCallback {
    void onActivityIdReceived( );
    void onActivityNotFound();
    void onError(Exception e);
}
