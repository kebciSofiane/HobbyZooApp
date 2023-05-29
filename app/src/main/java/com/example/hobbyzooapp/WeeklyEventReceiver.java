package com.example.hobbyzooapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WeeklyEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activityRef = database.getReference("Activity");

        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activityId = snapshot.child("activity_id").getValue(String.class);
                    DatabaseReference elementRef = activityRef.child(activityId);
                    elementRef.child("spent_time").setValue("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error if the request is canceled
            }
        });
    }
}

