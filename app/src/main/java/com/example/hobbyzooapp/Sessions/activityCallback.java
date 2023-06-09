package com.example.hobbyzooapp.Sessions;

interface ActivityCallback {
    void onActivityIdReceived( );
    void onActivityNotFound();
    void onError(Exception e);
}
