package com.example.hobbyzooapp;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class WeeklyEvent extends AppCompatActivity {

    LocalDate nextMonday, dateMondayData;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    String userId;
    int nextDayMondayUser, nextMonthMondayUser, nextYearMondayUser;
    private final int SCREEN_TIMEOUT = 1000;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        database = FirebaseDatabase.getInstance();
        nextMonday = getNextMonday();
        setNextMondayUser();


    }

    private void setNextMondayUser(){
        DatabaseReference userRef = database.getReference("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String user_id = dataSnapshot.getKey();
                    if(userId.equals(user_id)){
                        nextDayMondayUser = dataSnapshot.child("connectNextMondayDay").getValue(Integer.class);
                        nextMonthMondayUser = dataSnapshot.child("connectNextMondayMonth").getValue(Integer.class);
                        nextYearMondayUser = dataSnapshot.child("connectNextMondayYear").getValue(Integer.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dateMondayData = LocalDate.of(nextYearMondayUser, nextMonthMondayUser, nextDayMondayUser);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if(nextMonday.isAfter(dateMondayData)){
                                DatabaseReference userRef = database.getReference("Users").child(userId);
                                userRef.child("connectNextMondayDay").setValue(nextMonday.getDayOfMonth());
                                userRef.child("connectNextMondayMonth").setValue(Integer.parseInt(String.valueOf(nextMonday.getMonth().getValue())));
                                userRef.child("connectNextMondayYear").setValue(nextMonday.getYear());
                                modificationActivity();
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(WeeklyEvent.this, HomeActivity.class));
                                finish();
                            }
                        },SCREEN_TIMEOUT);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void modificationActivity(){
        DatabaseReference activityRef = database.getReference("Activity");
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activityId = snapshot.child("activity_id").getValue(String.class);
                    String user_id = snapshot.child("user_id").getValue(String.class);
                    int spentTime = Integer.parseInt(Objects.requireNonNull(snapshot.child("spent_time").getValue(String.class)));
                    int weeklyGoal = Integer.parseInt(Objects.requireNonNull(snapshot.child("weekly_goal").getValue(String.class)));
                    int animalFeeling = Integer.parseInt(Objects.requireNonNull(snapshot.child("feeling").getValue(String.class)));
                    DatabaseReference elementRef = activityRef.child(activityId);
                    if(userId.equals(user_id)){
                        elementRef.child("spent_time").setValue("0");
                        if(spentTime < weeklyGoal)
                            elementRef.child("feeling").setValue(Math.max(0, animalFeeling-1)+"");
                        else
                            elementRef.child("feeling").setValue(Math.min(HomeActivity.animalsFeeling.size()-1, animalFeeling+1)+"");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error if the request is canceled
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private LocalDate getNextMonday() {
        LocalDate currentDate;
        LocalDate nextMonday = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            nextMonday = currentDate.with(DayOfWeek.MONDAY);
            if (currentDate.compareTo(nextMonday) >= 0) {
                nextMonday = nextMonday.plusWeeks(1);
            }
        }
        return nextMonday;
    }
}
