package com.example.hobbyzooapp.Sessions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hobbyzooapp.Calendar.CalendarActivity;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

public class MyDailySessions extends AppCompatActivity {

    private ImageButton homeButton, addSessionButton, calendarButton;
    private Button editButton, validateButton;
    LocalDate localDate = CalendarUtils.selectedDate;
    MyDailySessionsAdapter adapter;
    Boolean isDeleteMode = Boolean.FALSE;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceSession;
    DatabaseReference referenceActivity;
    String uid;
    FirebaseAuth firebaseAuth;
    FirebaseUser user ;
    GridView sessionListView;
    int previousIsHome = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_daily_sessions);
        firebaseAuth = FirebaseAuth.getInstance();
        homeButton = findViewById(R.id.home_button);
        addSessionButton = findViewById(R.id.add_session_button);
        calendarButton = findViewById(R.id.calendar_button);
        editButton = findViewById(R.id.dailySessionPageEditButton);
        validateButton = findViewById(R.id.dailySessionPageValidateButton);
        sessionListView = findViewById(R.id.session_list_view);
        TextView dateSession = findViewById(R.id.dateSession);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        uid = user.getUid();
        referenceSession = database.getReference("Session");
        referenceActivity = database.getReference("Activity");
        String date = null;

        sessionCallBack();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = CalendarUtils.selectedDate.getDayOfMonth() + "/" + CalendarUtils.selectedDate.getMonth().getValue() + "/" + CalendarUtils.selectedDate.getYear();
        }
        dateSession.setText(date);



        homeButton.setOnClickListener(v -> {
            finishAffinity();
            openMainActivity();
            finish();
        });

        addSessionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyDailySessions.this, NewSession.class);
            intent.putExtra("previousActivity", 0);
            startActivity(intent);
            finish();
        });

        calendarButton.setOnClickListener(v ->{
            openCalendrarActivity();
            finish();
        });

        editButton.setOnClickListener(v -> {
            editButton.setVisibility(View.GONE);
            validateButton.setVisibility(View.VISIBLE);
            homeButton.setVisibility(View.GONE);
            addSessionButton.setVisibility(View.INVISIBLE);
            calendarButton.setVisibility(View.GONE);
            isDeleteMode = Boolean.TRUE;
            adapter.setIsDeleteMode(true);
            adapter.notifyDataSetChanged();

        });

        validateButton.setOnClickListener(v -> {
            editButton.setVisibility(View.VISIBLE);
            validateButton.setVisibility(View.GONE);
            homeButton.setVisibility(View.VISIBLE);
            addSessionButton.setVisibility(View.VISIBLE);
            calendarButton.setVisibility(View.VISIBLE);
            isDeleteMode = Boolean.FALSE;
            adapter.setIsDeleteMode(false);
            adapter.notifyDataSetChanged();

        });

    }

    private void sessionCallBack(){
        @SuppressLint("SetTextI18n") SessionsCallback callback = mySessions -> {
            adapter = new MyDailySessionsAdapter(MyDailySessions.this, mySessions, localDate, Boolean.FALSE);
            adapter.setOnItemClickListener(position -> {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_, null);
                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                TextView dialogText = dialogView.findViewById(R.id.dialogText);
                Button dialogButtonYes = dialogView.findViewById(R.id.dialogButtonRight);
                Button dialogButtonNo = dialogView.findViewById(R.id.dialogButtonLeft);

                dialogTitle.setText(adapter.getItem(position).getActivityName() + " - " + adapter.getItem(position).getTime());
                if (isDeleteMode.equals(Boolean.TRUE)){
                    dialogText.setText("Do you really want to delete ?");
                    dialogButtonYes.setText("Yes");
                    dialogButtonYes.setTextColor(Color.RED);
                    dialogButtonNo.setText("No");
                    dialogButtonNo.setTextColor(Color.GREEN);
                } else {
                    dialogText.setText("Do you want to start ?");
                    dialogButtonYes.setText("Yes");
                    dialogButtonYes.setTextColor(Color.GREEN);
                    dialogButtonNo.setText("No");
                    dialogButtonNo.setTextColor(Color.RED);
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyDailySessions.this);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                dialogButtonYes.setOnClickListener(v -> {
                    if (isDeleteMode.equals(Boolean.TRUE)){
                        referenceSession.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String sessionId = adapter.getItem(position).getSessionId();
                                referenceSession.child(sessionId).removeValue();
                                refresh();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        Intent intent = new Intent(MyDailySessions.this, RunSession.class);
                        intent.putExtra("activity_id", adapter.getItem(position).getActivityId());
                        intent.putExtra("session_id", adapter.getItem(position).getSessionId());
                        startActivity(intent);
                    }
                    dialog.dismiss();
                });
                dialogButtonNo.setOnClickListener(v -> dialog.dismiss());
            });
            sessionListView.setAdapter(adapter);
        };

        getSessions(callback);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openCalendrarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }


    public void refresh() {
        Intent intent = new Intent(MyDailySessions.this, MyDailySessions.class);
        startActivity(intent);
        finish();
    }

     private void getSessions(SessionsCallback callback) {

         ArrayList<Session> mySessions = new ArrayList<>();
         referenceSession.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                 String session_id = snapshot.child("session_id").getValue(String.class);
                 String session_duration = snapshot.child("session_duration").getValue(String.class);
                 String activity_id = snapshot.child("activity_id").getValue(String.class);
                 String session_day = snapshot.child("session_day").getValue(String.class);
                 String session_month = snapshot.child("session_month").getValue(String.class);
                 String session_year = snapshot.child("session_year").getValue(String.class);
                 String session_image = snapshot.child("session_picture").getValue(String.class);
                 String session_done = snapshot.child("session_done").getValue(String.class);


                 assert activity_id != null;
                 referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                     @RequiresApi(api = Build.VERSION_CODES.O)
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()) {
                             String activityName = dataSnapshot.child("activity_name").getValue(String.class).replace(",", " ");
                             assert session_duration != null;
                             int hourDuration = Integer.parseInt(session_duration)/60;
                             int minutesDuration = Integer.parseInt(session_duration)%60;
                             String mnemonicPet = dataSnapshot.child("activity_pet").getValue(String.class);
                             assert session_day != null;
                             assert session_month != null;
                             assert session_year != null;
                             mySessions.add(new Session(session_id,
                                                 activity_id,
                                                 activityName,
                                                 new Time(hourDuration,minutesDuration,0), Integer.parseInt(session_day), Integer.parseInt(session_month), Integer.parseInt(session_year),
                                                 session_image,
                                                 mnemonicPet,
                                                 session_done)
                                         );
                             callback.onSessionsLoaded(mySessions);
                         }
                     }
                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                         Log.w("TAG", "Data recovery error", databaseError.toException());
                     }
                 });
             }
         }
         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
             Log.w("TAG", "Data recovery error", databaseError.toException());
         }
     });
     }
}