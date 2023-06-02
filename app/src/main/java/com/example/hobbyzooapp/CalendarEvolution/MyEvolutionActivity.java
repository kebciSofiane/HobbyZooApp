package com.example.hobbyzooapp.CalendarEvolution;

import static com.example.hobbyzooapp.Calendar.CalendarUtils.daysInMonthArray;
import static com.example.hobbyzooapp.Calendar.CalendarUtils.monthYearFromDate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.Category.NewCategory;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.AccountManagement.ProfileActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyEvolutionActivity extends AppCompatActivity implements CalendarEvolutionAdapter.OnItemListener {

    Spinner chooseActivity;
    ArrayList<String> myActivities = new ArrayList<>();
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ImageButton todayMonthButton, homeButton, backButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    String selectedActivity;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evolution);

        database =FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        chooseActivity = findViewById(R.id.evolutionActivityChooseActivity);
        getActivities();


        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });


        todayMonthButton = findViewById(R.id.today_month);
        todayMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = LocalDate.now();
                setMonthView();
            }
        });

        backButton = findViewById(R.id.backButtonMyEvolution);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyEvolutionActivity.this, ProfileActivity.class));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MyEvolutionActivity.this, R.layout.spinner_evolution_activity, myActivities);
        chooseActivity.setAdapter(adapter);
        chooseActivity.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        selectedActivity = (String) chooseActivity.getSelectedItem();
                        DatabaseReference referenceActivity = database.getReference("Activity");

                        referenceActivity.orderByChild("activity_name").equalTo(selectedActivity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    System.out.println(selectedActivity);
                                    String category_id = snapshot.child("category_id").getValue(String.class);
                                    DatabaseReference referenceCategory = database.getReference("Category");
                                    System.out.println(category_id);

                                    referenceCategory.orderByChild("category_id").equalTo(category_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String category_color = snapshot.child("category_color").getValue(String.class);
                                                int color = Color.parseColor(category_color);
                                                chooseActivity.setBackgroundColor(color);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Gérez les erreurs de la récupération des données
                                        }
                                    });



                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Gérez les erreurs de la récupération des données
                            }
                        });
                        initWidgets();
                        CalendarUtils.selectedDate = LocalDate.now();
                        setMonthView();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });
    }

        ArrayList<String> getActivities () {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String user_id = user.getUid();
            ArrayList<String> activities = new ArrayList<>();
            DatabaseReference dataBaseActivityRef = FirebaseDatabase.getInstance().getReference().child("Activity");

            dataBaseActivityRef.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String activity_name = snapshot.child("activity_name").getValue(String.class);
                        activities.add(activity_name);
                    }
                    myActivities =activities;
                    if (activities.size()!=0)   {
                    selectedActivity=activities.get(0) ;
                    setAdapter();
                }
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

        CalendarEvolutionAdapter calendarAdapter = new CalendarEvolutionAdapter(daysInMonth, this,evenDayColor, selectedActivity);
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
        if(date != null) {
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
                                                date.getDayOfMonth() == sessionDate.getDayOfMonth() &&
                                                date.getYear() == sessionDate.getYear()) {
                                            assert activityName != null;
                                            if (activityName.equals(selectedActivity)) {

                                                if (!session_image.isEmpty()) {

                                                    Intent intent = new Intent(MyEvolutionActivity.this, DateMemory.class);
                                                    intent.putExtra("day", sessionDate.getDayOfMonth());
                                                    intent.putExtra("month", sessionDate.getMonth().getValue());
                                                    intent.putExtra("year", sessionDate.getYear());
                                                    intent.putExtra("activity_name", selectedActivity);
                                                    startActivity(intent);
                                                }

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



        }
    }



    public void openMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();}

}