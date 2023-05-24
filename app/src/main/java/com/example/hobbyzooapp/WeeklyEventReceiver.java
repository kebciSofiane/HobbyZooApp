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
        DatabaseReference tableRef = database.getReference("Activity");
        DatabaseReference databaseReferenceChild = FirebaseDatabase.getInstance().getReference().child("Activity");
        databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    DatabaseReference elementRef = tableRef.child(activity_id);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("spent_time", "0");
                    elementRef.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérez l'erreur en cas d'annulation de la requête
            }
        });
    }
}
