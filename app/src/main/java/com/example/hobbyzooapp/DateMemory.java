package com.example.hobbyzooapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hobbyzooapp.Sessions.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


public class DateMemory extends AppCompatActivity {
    TextView dateView;
    LocalDate date;
    ImageView memoryImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_memory);


        Intent intent = getIntent();
        int day = intent.getIntExtra("day",0);
        int month = intent.getIntExtra("month",0);
        int year = intent.getIntExtra("year",0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             date = LocalDate.of(year,
                    month,
                    day);
        }


        dateView=findViewById(R.id.memoryDate);
        memoryImage=findViewById(R.id.memoryImage);
        dateView.setText(date.toString());

        showMemories();
    }



    private void showMemories(){


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<String> myMemories = new ArrayList<>();
        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_done = snapshot.child("session_done").getValue(String.class);
                    String session_image = snapshot.child("session_picture").getValue(String.class);
                    LocalDate sessionDate = null; //todo vérfiier le null
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        assert session_year != null;
                        assert session_month != null;
                        assert session_day != null;
                        sessionDate = LocalDate.of(Integer.parseInt(session_year), Integer.parseInt(session_month), Integer.parseInt(session_day));
                    }


                    assert session_done != null;
                    if (session_done.equals("TRUE")) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (date.getMonth()==sessionDate.getMonth() &&
                                    date.getDayOfMonth() == sessionDate.getDayOfMonth() &&
                                    date.getYear() == sessionDate.getYear()) {
                                myMemories.add(session_image);

                            }
                        }

                    }
                }

                System.out.println("huhuidcchiosdhé"+myMemories.get(0));
                Glide.with(DateMemory.this)
                        .load(myMemories.get(0))
                        .into(memoryImage);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });

    }
}