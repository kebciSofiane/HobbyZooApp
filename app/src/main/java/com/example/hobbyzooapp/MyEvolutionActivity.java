package com.example.hobbyzooapp;

import static com.example.hobbyzooapp.Calendar.CalendarUtils.daysInMonthArray;
import static com.example.hobbyzooapp.Calendar.CalendarUtils.monthYearFromDate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.Sessions.MyDailySessions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyEvolutionActivity extends AppCompatActivity implements CalendarEvolutionAdapter.OnItemListener {

    Spinner chooseActivity;
    ArrayList<String> myActivities = new ArrayList<>();
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button todaySessionButton;
    private Button todayMonthButton;
    private ImageButton homeButton;
    FirebaseAuth firebaseAuth;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evolution);
        chooseActivity = findViewById(R.id.evolutionActivityChooseActivity);
        getActivities();



        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();



        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });


    }







    void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MyEvolutionActivity.this, android.R.layout.simple_list_item_1, myActivities);
        chooseActivity.setAdapter(adapter);
    }
        ArrayList<String> getActivities () {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String user_id = user.getUid();
            ArrayList<String> activities = new ArrayList<>();
            DatabaseReference dataBaseActivityRef = FirebaseDatabase.getInstance().getReference().child("Activity");

            dataBaseActivityRef.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String activity_name = snapshot.child("activity_name").getValue(String.class);
                        activities.add(activity_name);
                    }
                    myActivities =activities;
                    setAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
                }
            });

            return activities;
    }


    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);
        int evenDayColor = getResources().getColor(R.color.evenDayColor);

        CalendarEvolutionAdapter calendarAdapter = new CalendarEvolutionAdapter(daysInMonth, this,evenDayColor);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {






            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Session");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();
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



                        if (session_done.equals("TRUE")) {

                            DatabaseReference referenceActivity = database.getReference("Activity");

                            referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String activityName = dataSnapshot.child("activity_name").getValue(String.class);
                                        LocalDate sessionDate = LocalDate.of(Integer.parseInt(session_year), Integer.parseInt(session_month), Integer.parseInt(session_day));

                                        if (date.getMonth() == sessionDate.getMonth() &&
                                                date.getDayOfMonth() == sessionDate.getDayOfMonth()&&
                                                date.getYear() == sessionDate.getYear()) {

                                            if (!session_image.isEmpty()) {

                                                Intent intent = new Intent(MyEvolutionActivity.this, DateMemory.class);
                                                intent.putExtra("day",sessionDate.getDayOfMonth());
                                                intent.putExtra("month",sessionDate.getMonth().getValue());
                                                intent.putExtra("year",sessionDate.getYear());

                                                startActivity(intent);
                                            }

                                        }
                                    } else {
                                        // L'activité n'existe pas dans la base de données
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Une erreur s'est produite lors de la récupération des données
                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
                }
            });



















//            CalendarUtils.selectedDate = date;
//            MyDailySessions.localDate=date;
//            openTodaySessions();

        }
    }


    public void openTodaySessions(){
        Intent intent = new Intent(this, MyDailySessions.class);
        startActivity(intent);}

    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();}

}